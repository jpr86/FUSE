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

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.logging.log4j.*;

/**
 * A soon-to-be-improved implementation of a Font Manager.
 * @author Jason C. HandUber
 */
public class FontManager
{
    private static final Logger logger = LogManager.getLogger(FontManager.class);
    
    private final ArrayList<HashMap<HashSet<String>, File>> keys2ttf = new ArrayList<>();
    
    /**
     * A soon-to-be-improved Font Manager
	 *
     */
    public FontManager()
    {
    }
    
    /**
     * Returns fuse.ttf, which should be in the classpath under /fuse_icons (ie
     * contained in fuse_icons.jar)
     * @return fuse.TTF (true type font) as a java.awt.Font object
	 *
     */
    public Font getDefaultFont()
    {
        Font defaultFont = null;
        try
        {
            defaultFont =  Font.createFont(Font.TRUETYPE_FONT, new File("fuse.ttf"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return defaultFont;
    }
    
    /**
     *  Method file2ArrayList
     *
     * @param  file_data  an ArrayList
     * @return            an ArrayList
     * @version           10/15/2004
     * @author            Jason C. HandUber
     */
    private ArrayList<String> file2ArrayList(File file)
    {
        ArrayList<String> file_data = new ArrayList<>();
        
        try
        {
            FileReader fr = new FileReader(file);
            BufferedReader inFile = new BufferedReader(fr);
            
            String line = inFile.readLine();
            
            while (line != null && line.trim().length()!=0)
            {
                file_data.add(line);
                line = inFile.readLine();
            }
            
            inFile.close();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("ERROR, FileNotFoundException - Could not open file: " + file);
            e.printStackTrace();
            file_data = null;
            //errors are dealt with by checking for null
        }
        catch (IOException e)
        {
            System.err.println("ERROR, IOException - Could not read from file: " + file);
            e.printStackTrace();
            file_data = null;
            //errors are dealt with by checking for null
        }
        
        return file_data;
    }
}
