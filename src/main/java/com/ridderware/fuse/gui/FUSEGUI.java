/*
 * 
 * Fast Universal Simulation Engine (FUSE)
 *
 * Copyright 2014 Jeff Ridder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ridderware.fuse.gui;

import com.ridderware.fuse.Space;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.*;

/**
 * This is the main slave class to GUIUniverse that handles all the GUI stuff.
 * @author Jason HandUber
 */
public class FUSEGUI extends javax.swing.JFrame
{
    private static final Logger logger = LogManager.getLogger(FUSEGUI.class);
    
    /**
     * Any properties used by the FUSEGUI
     */
    public FUSEGUI_Properties props = new FUSEGUI_Properties();
    
    private final String versionInfo = "FUSE GUI.";
    
    //simulation speed multiplication factor
    private int last_xfactor_numerator = 1;
    private int last_xfactor_denominator = 1;
    private final double minXfactor = 1./256.;
    private final double maxXfactor = 8192;
    
    private double lastUpdateTime_sim;
    
    private boolean step = false;
    private Boolean pause = new Boolean(Boolean.TRUE);
    private Boolean restart = new Boolean(Boolean.FALSE);
    private Boolean realtime = new Boolean(Boolean.FALSE);
    
    private Space space;
    private GUIUniverse universe;
    
    private HashSet<ViewFrame> newViews = new HashSet<ViewFrame>();
    private HashMap<String, ViewFrame> windowName2viewFrame =
            new HashMap<String, ViewFrame>();
    
    private DecimalFormat timefmt = new DecimalFormat("#######.#####");
    
    private static FUSEGUI one = new FUSEGUI();
    
    private FUSEGUI()
    {}
    
    /**
     * Get this FUSEGUI object
     * @return Returns this FUSEGUI object
     */
    public static FUSEGUI getGUI()
    {
        return one;
    }
    
    /**
     * Initializes the FUSEGUI with required references (GUI_Universe)
 and initializes Swing components and makes the top-level component
 visible.
     * @param universe the GUI_Universe this FUSEGUI will be working for
     */
    public void initialize(GUIUniverse universe)
    {
        try
        {
            this.universe = universe;
            this.space = universe.getSpace();
            initComponents();
            customInit();
            setVisible(true);
        }
        catch(Exception e)
        {
            logger.error("here2");
            e.printStackTrace();
        }
    }
    
    /**
     * Call prior to any GUI stepping
     */
    public void prestep()
    {
        Iterator<ViewFrame> it = newViews.iterator();
        while (it.hasNext())
        {
            it.next().setVisible(true);
            it.remove();
        }
    }
    
    /**
     * GUI Step
     */
    public void step()
    {
        updateMisc();
        updateViews();
        doDelay();
        doPlayPauseStep();
    }
    
    private void doPlayPauseStep()
    {
        try
        {
            synchronized(pause)
            {
                if (pause && !restart)
                {
                    //in case we reset while in pause, we need to break this lock
                    try
                    {
                        pause.wait();
                    }
                    catch(InterruptedException e)
                    {}
                    pause = false;
                }
            }
            
            if (restart || universe.isDone())
            {
                while (!restart)
                {
                    Thread.currentThread().sleep(50);
                }
                
                pause = true;
                step = false;
                restart = false;
                this.timeProgressBar.setValue(0);
                this.timeTextField.setText(timefmt.format(universe.getStartTime()));
                repaint();
                universe.executeScenario();
            }
        }
        catch(Exception e)
        {e.printStackTrace();}
        
        
        if (step)
        {
            setPause(true);
        }
    }
    
    private void doDelay()
    {
        double deltaSim = universe.getCurrentTime() -lastUpdateTime_sim;
        
        if (realtimejRadioButton.isSelected())
        {
            try
            {
                double delayInSec = deltaSim/(last_xfactor_numerator/last_xfactor_denominator);
                
                //Math.round() will round Infinity to Long.MaxValue
                long sleeptime = Math.round(delayInSec*1000);
                
                //enforce a maximum sleep time of 30 seconds
                if (sleeptime > 30000)
                {
                    sleeptime = 30000;
                }

                if (sleeptime == 30000)
                {
                    multiplierjLabel.setToolTipText("Delaying for maximum delay time of 30 seconds");
                }
                else if (last_xfactor_numerator/last_xfactor_denominator > 1)
                {
                    multiplierjLabel.setToolTipText("Delaying for "+(sleeptime/10000.)+" seconds in order to approximate "+ last_xfactor_numerator+"X times faster than realtime");
                }
                else if ((last_xfactor_numerator/last_xfactor_denominator) < 1)
                {
                    multiplierjLabel.setToolTipText("Delaying for "+(sleeptime/10000.)+" seconds in order to approximate "+ last_xfactor_numerator+"/"+last_xfactor_denominator+"X times slower than realtime");
                }
                else if ((last_xfactor_numerator/last_xfactor_denominator) == 1)
                {
                    multiplierjLabel.setToolTipText("Delaying for "+(sleeptime/10000.)+" seconds in order to approximate realtime");
                }                
                
                if (sleeptime > 0)
                {
                    Thread.currentThread().sleep(sleeptime);
                }
            }
            catch (InterruptedException e)
            {
                logger.info("Breaking delay.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            multiplierjLabel.setText(null);
        }
        lastUpdateTime_sim = universe.getCurrentTime();
    }
    
    
    private void updateViews()
    {
        for (ViewFrame vp : windowName2viewFrame.values())
        {
            if (vp.isDrawable())
            {
                vp.update();
            }
        }
    }
    
    /**
     * Repaints its borders and background and then prompts all ViewFrames to repaint
     * themselves
     * @param g the Graphics object
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        updateViews();
    }
    
    private void updateMisc()
    {
        timeTextField.setText(timefmt.format(universe.getCurrentTime()));
        timeProgressBar.setValue((int) (100* (universe.getCurrentTime()-universe.getStartTime())
        /   (universe.getEndTime()-universe.getStartTime())) );
        controlJInternalFrame.validate();
    }
    
    /**
     * Creates a window with the specified View
     * @param view the View
     */
    public void createWindow(ViewFrame view)
    {
        view.setSpace(space);
        view.initialize();
        
        
        if (windowName2viewFrame.containsKey(view.getName()) || (windowName2viewFrame.containsValue(view)))
        {
            logger.warn("Duplicate view or view name violation");
        }
        else
        {
            windowName2viewFrame.put(view.getName(), view);
            mainDesktopPane.add(view.getFrame(), javax.swing.JLayeredPane.DEFAULT_LAYER);
            
            //These JMenuItems are put together @ runtime
            JMenuItem newViewMenuItem = new JMenuItem();
            newViewMenuItem.setText(view.getName());
            newViewMenuItem.setToolTipText("Toggles "+view.getName()+".setVisible()");
            newViewMenuItem.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    viewWindowActionPerformmed(evt);
                }
            });
            
            viewsMenu.add(newViewMenuItem);
            newViews.add(view);
        }
    }
    
    public ViewFrame getViewFrameByTitle(String title)
    {
        return windowName2viewFrame.get(title);
    }
    
    /**
     * Adds a reflection frame to the top level component and sets it visible
     * @param rf the ReflectionFrame
     */
    public void addReflectionFrame(ReflectorFrame rf)
    {
        mainDesktopPane.add(rf, javax.swing.JLayeredPane.DEFAULT_LAYER, 0);
        rf.setVisible(true);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        run_realbuttonGroup = new javax.swing.ButtonGroup();
        mainDesktopPane = new javax.swing.JDesktopPane();
        controlJInternalFrame = new javax.swing.JInternalFrame();
        jToolBar1 = new javax.swing.JToolBar();
        playjButton = new javax.swing.JButton();
        stepjButton = new javax.swing.JButton();
        pausejButton = new javax.swing.JButton();
        restartjButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        runjRadioButton = new javax.swing.JRadioButton();
        realtimejRadioButton = new javax.swing.JRadioButton();
        speedUpjButton = new javax.swing.JButton();
        slowDownjButton = new javax.swing.JButton();
        multiplierjLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        timeTextField = new javax.swing.JTextField();
        timeProgressBar = new javax.swing.JProgressBar();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        quitMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        toggleBufferjMenuItem = new javax.swing.JMenuItem();
        toggleSpacejMenuItem = new javax.swing.JMenuItem();
        toggleBoundingBoxesjMenuItem = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        view_controlsMenuItem = new javax.swing.JMenuItem();
        viewAllGuiableData = new javax.swing.JMenuItem();
        viewsMenu = new javax.swing.JMenu();
        newViewjMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        helpJMenu = new javax.swing.JMenu();
        aboutjMenuItem = new javax.swing.JMenuItem();
        supportjMenuItem = new javax.swing.JMenuItem();
        thanksjMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("FUSE");
        setFont(new java.awt.Font("Aharoni CLM", 0, 10)); // NOI18N
        setName("rootContainer"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainDesktopPane.setBackground(java.awt.Color.lightGray);
        mainDesktopPane.setDoubleBuffered(true);
        mainDesktopPane.setDragMode(javax.swing.JDesktopPane.OUTLINE_DRAG_MODE);
        mainDesktopPane.setLayout(null);

        controlJInternalFrame.setClosable(true);
        controlJInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        controlJInternalFrame.setResizable(true);
        controlJInternalFrame.setTitle("Controls");
        controlJInternalFrame.setDoubleBuffered(true);
        controlJInternalFrame.setVisible(true);

        jToolBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar1.setFloatable(false);
        jToolBar1.setDoubleBuffered(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(425, 50));
        jToolBar1.setMinimumSize(new java.awt.Dimension(181, 18));
        jToolBar1.setPreferredSize(new java.awt.Dimension(189, 18));

        playjButton.setForeground(new java.awt.Color(255, 255, 255));
        playjButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play16.gif"))); // NOI18N
        playjButton.setToolTipText("Play");
        playjButton.setAutoscrolls(true);
        playjButton.setDoubleBuffered(true);
        playjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playjButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(playjButton);

        stepjButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepForward16.gif"))); // NOI18N
        stepjButton.setToolTipText("Take a step");
        stepjButton.setDoubleBuffered(true);
        stepjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepjButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(stepjButton);

        pausejButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Pause16.gif"))); // NOI18N
        pausejButton.setToolTipText("Pause");
        pausejButton.setDoubleBuffered(true);
        pausejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pausejButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(pausejButton);

        restartjButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Refresh16.gif"))); // NOI18N
        restartjButton.setToolTipText("Reset the simulation");
        restartjButton.setDoubleBuffered(true);
        restartjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartjButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(restartjButton);

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));
        jSeparator1.setDoubleBuffered(true);
        jSeparator1.setMaximumSize(new java.awt.Dimension(30, 50));
        jToolBar1.add(jSeparator1);

        run_realbuttonGroup.add(runjRadioButton);
        runjRadioButton.setSelected(true);
        runjRadioButton.setToolTipText("Run the simulation as fast as possible");
        runjRadioButton.setBorderPainted(true);
        runjRadioButton.setDoubleBuffered(true);
        runjRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runjRadioButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(runjRadioButton);

        run_realbuttonGroup.add(realtimejRadioButton);
        realtimejRadioButton.setToolTipText("Run the simulation at a multiple of real-time");
        realtimejRadioButton.setBorderPainted(true);
        realtimejRadioButton.setDoubleBuffered(true);
        realtimejRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realtimejRadioButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(realtimejRadioButton);

        speedUpjButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/navigation/Up16.gif"))); // NOI18N
        speedUpjButton.setToolTipText("Speed up simulation");
        speedUpjButton.setDoubleBuffered(true);
        speedUpjButton.setEnabled(false);
        speedUpjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speedUpjButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(speedUpjButton);

        slowDownjButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/navigation/Down16.gif"))); // NOI18N
        slowDownjButton.setToolTipText("Slow down simulation");
        slowDownjButton.setDoubleBuffered(true);
        slowDownjButton.setEnabled(false);
        slowDownjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                slowDownjButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(slowDownjButton);

        multiplierjLabel.setDoubleBuffered(true);
        multiplierjLabel.setMaximumSize(new java.awt.Dimension(50, 3000));
        jToolBar1.add(multiplierjLabel);

        timeLabel.setText("Time:");
        timeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 1));
        timeLabel.setDoubleBuffered(true);
        timeLabel.setMaximumSize(new java.awt.Dimension(40, 14));
        timeLabel.setMinimumSize(new java.awt.Dimension(40, 1));
        timeLabel.setPreferredSize(new java.awt.Dimension(35, 1));
        jToolBar1.add(timeLabel);
        timeLabel.getAccessibleContext().setAccessibleName("Controls");

        timeTextField.setEditable(false);
        timeTextField.setColumns(1);
        timeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        timeTextField.setText("0.0000");
        timeTextField.setToolTipText("Current simulation time (seconds)");
        timeTextField.setBorder(null);
        timeTextField.setDoubleBuffered(true);
        timeTextField.setMaximumSize(new java.awt.Dimension(150, 2147483647));
        timeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeTextFieldActionPerformed(evt);
            }
        });
        jToolBar1.add(timeTextField);

        controlJInternalFrame.getContentPane().add(jToolBar1, java.awt.BorderLayout.CENTER);

        timeProgressBar.setToolTipText("Displays percent complete (current time/end time (start time assumed 0))");
        timeProgressBar.setBorderPainted(false);
        timeProgressBar.setDoubleBuffered(true);
        timeProgressBar.setPreferredSize(new java.awt.Dimension(32000, 14));
        controlJInternalFrame.getContentPane().add(timeProgressBar, java.awt.BorderLayout.NORTH);

        mainDesktopPane.add(controlJInternalFrame);
        controlJInternalFrame.setBounds(0, 0, 480, 110);

        getContentPane().add(mainDesktopPane, java.awt.BorderLayout.CENTER);
        mainDesktopPane.getAccessibleContext().setAccessibleName("Controls");

        fileMenu.setText("File");

        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(quitMenuItem);

        mainMenuBar.add(fileMenu);

        optionsMenu.setText("Options");

        toggleBufferjMenuItem.setText("Toggle Buffer Box");
        toggleBufferjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleBufferjMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(toggleBufferjMenuItem);

        toggleSpacejMenuItem.setText("Toggle Space Box");
        toggleSpacejMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleSpacejMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(toggleSpacejMenuItem);

        toggleBoundingBoxesjMenuItem.setText("Toggle Bounding Boxes");
        toggleBoundingBoxesjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleBoundingBoxesjMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(toggleBoundingBoxesjMenuItem);

        mainMenuBar.add(optionsMenu);

        windowMenu.setText("Windows");
        windowMenu.setToolTipText("View standard windows (control, data frames, etc.)");

        view_controlsMenuItem.setText("Controls");
        view_controlsMenuItem.setToolTipText("View the control frame which contains the play/pause/step buttons, progress bar, delay controls, and displays the current simulation time");
        view_controlsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_controlsMenuItemActionPerformed(evt);
            }
        });
        windowMenu.add(view_controlsMenuItem);

        viewAllGuiableData.setText("All Data Frames");
        viewAllGuiableData.setToolTipText("Brings up one window for each registered GUIable, displaying the user specified method data");
        viewAllGuiableData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAllGuiableDataActionPerformed(evt);
            }
        });
        windowMenu.add(viewAllGuiableData);

        mainMenuBar.add(windowMenu);

        viewsMenu.setText("Views");

        newViewjMenuItem.setText("Create View");
        newViewjMenuItem.setToolTipText("Allows you to create a view on the fly.");
        newViewjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newViewjMenuItemActionPerformed(evt);
            }
        });
        viewsMenu.add(newViewjMenuItem);
        viewsMenu.add(jSeparator3);

        mainMenuBar.add(viewsMenu);

        helpJMenu.setText("Help");

        aboutjMenuItem.setText("About");
        aboutjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutjMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(aboutjMenuItem);

        supportjMenuItem.setText("Report Bug / Request Feature");
        supportjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supportjMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(supportjMenuItem);

        thanksjMenuItem.setText("Artistic Support");
        thanksjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thanksjMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(thanksjMenuItem);

        mainMenuBar.add(helpJMenu);

        setJMenuBar(mainMenuBar);
        mainMenuBar.getAccessibleContext().setAccessibleName("Controls");

        setSize(new java.awt.Dimension(790, 730));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quitMenuItemActionPerformed
    {//GEN-HEADEREND:event_quitMenuItemActionPerformed
        universe.killAll();
        this.dispose();
        System.exit(1);
    }//GEN-LAST:event_quitMenuItemActionPerformed
    
    private void thanksjMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_thanksjMenuItemActionPerformed
    {//GEN-HEADEREND:event_thanksjMenuItemActionPerformed
        JOptionPane.showMessageDialog(this, "<html>Special Thanks to Jean Paul Shottsborgh and Colonel Tom Mouat for Artistic Contributions.", "Artistic Contributions", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_thanksjMenuItemActionPerformed
    
    private void supportjMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_supportjMenuItemActionPerformed
    {//GEN-HEADEREND:event_supportjMenuItemActionPerformed
        JOptionPane.showMessageDialog(this, "Please submit all requests, including version information and duplicatable recipe to receive the error to ...", "Report Bugs or Submit Feature Requests", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_supportjMenuItemActionPerformed
    
    private void aboutjMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutjMenuItemActionPerformed
    {//GEN-HEADEREND:event_aboutjMenuItemActionPerformed
        JOptionPane.showMessageDialog(this,versionInfo, "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutjMenuItemActionPerformed
    
    private void toggleBoundingBoxesjMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_toggleBoundingBoxesjMenuItemActionPerformed
    {//GEN-HEADEREND:event_toggleBoundingBoxesjMenuItemActionPerformed
        Painter.getPainter().togglePaintBounds();
        repaint();
    }//GEN-LAST:event_toggleBoundingBoxesjMenuItemActionPerformed
    
    private void toggleBufferjMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_toggleBufferjMenuItemActionPerformed
    {//GEN-HEADEREND:event_toggleBufferjMenuItemActionPerformed
        Painter.getPainter().toggleBuffer();
        repaint();
    }//GEN-LAST:event_toggleBufferjMenuItemActionPerformed
    
    private void toggleSpacejMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_toggleSpacejMenuItemActionPerformed
    {//GEN-HEADEREND:event_toggleSpacejMenuItemActionPerformed
        Painter.getPainter().toggleSpace();
        repaint();
    }//GEN-LAST:event_toggleSpacejMenuItemActionPerformed
    
    private void realtimejRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_realtimejRadioButtonActionPerformed
    {//GEN-HEADEREND:event_realtimejRadioButtonActionPerformed
        if (last_xfactor_denominator >= 1)
        {
            multiplierjLabel.setText(last_xfactor_numerator+"X");
        }
        else
        {
            multiplierjLabel.setText(last_xfactor_numerator+"/"+ last_xfactor_denominator+"X");
        }
        
        slowDownjButton.setEnabled(true);
        speedUpjButton.setEnabled(true);
    }//GEN-LAST:event_realtimejRadioButtonActionPerformed
    
    private void runjRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_runjRadioButtonActionPerformed
    {//GEN-HEADEREND:event_runjRadioButtonActionPerformed
        slowDownjButton.setEnabled(false);
        speedUpjButton.setEnabled(false);
        multiplierjLabel.setToolTipText(null);
    }//GEN-LAST:event_runjRadioButtonActionPerformed
    
    private void newViewjMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newViewjMenuItemActionPerformed
    {//GEN-HEADEREND:event_newViewjMenuItemActionPerformed
        /*
        logger.info("buildling the dialog");
        NewViewJDialog newView = new NewViewJDialog(this, true, universe.getAllGUIables());
        logger.info("packing");
        newView.pack();
        logger.info("showing");
        createWindow(newView.doDialog());
        logger.info("getting data");
         **/
    }//GEN-LAST:event_newViewjMenuItemActionPerformed
    
    private void restartjButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_restartjButtonActionPerformed
    {//GEN-HEADEREND:event_restartjButtonActionPerformed
        restart = true;
        synchronized(pause)
        {
            pause.notify();
        }
    }//GEN-LAST:event_restartjButtonActionPerformed
    
    private void playjButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playjButtonActionPerformed
    {//GEN-HEADEREND:event_playjButtonActionPerformed
        step = false;
        synchronized(pause)
        {
            pause.notify();
        }
    }//GEN-LAST:event_playjButtonActionPerformed
    
    private void stepjButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stepjButtonActionPerformed
    {//GEN-HEADEREND:event_stepjButtonActionPerformed
        step = true;
        synchronized(pause)
        {
            pause.notify();
        }
    }//GEN-LAST:event_stepjButtonActionPerformed
    
    private void view_controlsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_view_controlsMenuItemActionPerformed
    {//GEN-HEADEREND:event_view_controlsMenuItemActionPerformed
        controlJInternalFrame.setVisible(true);
    }//GEN-LAST:event_view_controlsMenuItemActionPerformed
    
    private void pausejButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pausejButtonActionPerformed
    {//GEN-HEADEREND:event_pausejButtonActionPerformed
        if (!getPause())
            setPause(true);
    }//GEN-LAST:event_pausejButtonActionPerformed
    
    private void slowDownjButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_slowDownjButtonActionPerformed
    {//GEN-HEADEREND:event_slowDownjButtonActionPerformed
        changeUp(false);
    }//GEN-LAST:event_slowDownjButtonActionPerformed
    
    private void speedUpjButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_speedUpjButtonActionPerformed
    {//GEN-HEADEREND:event_speedUpjButtonActionPerformed
        changeUp(true);
    }//GEN-LAST:event_speedUpjButtonActionPerformed
    
    private synchronized void changeUp(boolean up)
    {
        if (up)
        {
            if (last_xfactor_numerator/last_xfactor_denominator >= 1)
            {
                last_xfactor_numerator*=2;
                if (last_xfactor_numerator/last_xfactor_denominator > maxXfactor)
                {
                    last_xfactor_numerator/=2;
                }
            }
            else
            {
                last_xfactor_denominator/=2;
                
                if (last_xfactor_numerator/last_xfactor_denominator > maxXfactor)
                {
                    last_xfactor_denominator*=2;
                }
            }
        }
        else
        {
            if (last_xfactor_numerator/last_xfactor_denominator <= 1)
            {
                last_xfactor_denominator*=2;
                
                if ((double)last_xfactor_numerator/last_xfactor_denominator < minXfactor)
                {
                    last_xfactor_denominator/=2;
                }
            }
            else
            {
                last_xfactor_numerator/=2;
                if (((double)last_xfactor_numerator/last_xfactor_denominator) < minXfactor)
                {
                    last_xfactor_numerator*=2;
                }
            }
        }
        
        if (last_xfactor_denominator == 1)
        {
            multiplierjLabel.setText(last_xfactor_numerator+"X");
        }
        else
        {
            multiplierjLabel.setText(last_xfactor_numerator+"/"+ last_xfactor_denominator+"X");
        }
    }
    
    private void viewAllGuiableDataActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_viewAllGuiableDataActionPerformed
    {//GEN-HEADEREND:event_viewAllGuiableDataActionPerformed
        for (ViewFrame frame : windowName2viewFrame.values())
        {
            ((ViewFrame)frame).viewAllGUIablesDataFrames();
        }
    }//GEN-LAST:event_viewAllGuiableDataActionPerformed
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        props.save();
    }//GEN-LAST:event_formWindowClosing

    private void timeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timeTextFieldActionPerformed
    
    
    /**
     * Anytime we create a ViewFrame window, we add a menu item in our
     * GUI. Clicking that menu item executes this code, setting the
     * ViewFrame to visible.
     */
    private void viewWindowActionPerformmed(java.awt.event.ActionEvent evt)
    {
        ViewFrame view = windowName2viewFrame.get(evt.getActionCommand());
        view.setVisible(true);
    }
    
    
    private synchronized void setPause(Boolean pause)
    {
        this.pause = pause;
    }
    
    private synchronized Boolean getPause()
    {
        return this.pause;
    }
    
    /**
     * Initially we load up the GUI, we should check and see if there
     * are any saved properties the user has inputted (ex. Delay Time).
     * If so, load those properties and use them instead of the defaults.
     */
    private void customInit()
    {
        logger.debug("customInit");
        if (this.timeProgressBar!=null)
        {
            logger.debug("setting value to 0");
            timeProgressBar.setValue(0);
            timeProgressBar.repaint();
        }
        lastUpdateTime_sim = universe.getCurrentTime();
        props.load();
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutjMenuItem;
    private javax.swing.JInternalFrame controlJInternalFrame;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpJMenu;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JDesktopPane mainDesktopPane;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JLabel multiplierjLabel;
    private javax.swing.JMenuItem newViewjMenuItem;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JButton pausejButton;
    private javax.swing.JButton playjButton;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JRadioButton realtimejRadioButton;
    private javax.swing.JButton restartjButton;
    private javax.swing.ButtonGroup run_realbuttonGroup;
    private javax.swing.JRadioButton runjRadioButton;
    private javax.swing.JButton slowDownjButton;
    private javax.swing.JButton speedUpjButton;
    private javax.swing.JButton stepjButton;
    private javax.swing.JMenuItem supportjMenuItem;
    private javax.swing.JMenuItem thanksjMenuItem;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JProgressBar timeProgressBar;
    private javax.swing.JTextField timeTextField;
    private javax.swing.JMenuItem toggleBoundingBoxesjMenuItem;
    private javax.swing.JMenuItem toggleBufferjMenuItem;
    private javax.swing.JMenuItem toggleSpacejMenuItem;
    private javax.swing.JMenuItem viewAllGuiableData;
    private javax.swing.JMenuItem view_controlsMenuItem;
    private javax.swing.JMenu viewsMenu;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables
    
    
    /**
     * Any properties belonging to the GUI
     */
    public class FUSEGUI_Properties
    {
        FileOutputStream fos = null;
        ObjectOutputStream outStream = null;
        FileInputStream fis = null;
        ObjectInputStream inStream = null;
        
        
        /**
         * Saves any properties belonging to the GUI
         * (either to a default location or a specified file)
         */
        public void save()
        {
            try
            {
                fos = new FileOutputStream(new java.io.File("/tmp/fuse.gui.properties"));
                outStream = new ObjectOutputStream(fos);
                //outStream.writeLong(delayTime);
                outStream.flush();
                fos.flush();
                outStream.close();
                fos.close();
            }
            catch (Exception e)
            {e.printStackTrace();}
        }
        
        /**
         * Loads any properties belonging to the GUI
         * (either from the default location or a specified file)
         */
        public void load()
        {
            try
            {
                fis = new FileInputStream(new java.io.File("/tmp/fuse.gui.properties"));
                inStream = new ObjectInputStream(fis);
                //delayTime = inStream.readLong();
                inStream.close();
                fis.close();
            }
            catch (FileNotFoundException e)
            {}
            catch (Exception e)
            {e.printStackTrace();}
        }
    }
}
