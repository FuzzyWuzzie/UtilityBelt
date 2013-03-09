package com.mcnsa.utilitybelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONValue;

import net.minecraft.client.Minecraft;

public class MenuLoader {
	public static HashMap<String, UtilityMenu> loadMenus() {
		HashMap<String, UtilityMenu> menus = new HashMap<String, UtilityMenu>();
		try {
			File cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
			File cfgfile = new File(cfgdir, "UtilityBelt.menus.json");
			if(!cfgfile.exists()) {
				// if it doesn't exist, we don't have a menu
				UtilityBelt.menuEnabled = false;
				UtilityBelt.displayErrorString("menus file does not exist!");
				return menus;
			}

			// load the file
			try {
				String lineSep = System.getProperty("line.separator");
				FileInputStream fin = new FileInputStream(cfgfile);
				BufferedReader input = new BufferedReader(new InputStreamReader(fin));
				String nextLine = "";
				StringBuffer sb = new StringBuffer();
				while((nextLine = input.readLine()) != null) {
					sb.append(nextLine);
					sb.append(lineSep);
				}
				
				// start parsing
				HashMap<String, Object> obj = (HashMap<String, Object>)JSONValue.parse(sb.toString());
				
				// grab the objects
				if(obj != null) {
					// loop through the menus
					Boolean first = true;
					for(String menuName: obj.keySet()) {
						UtilityMenu menu = new UtilityMenu(menuName);
						
						// now get a list of all it's parts
						ArrayList<ArrayList<String>> items = (ArrayList<ArrayList<String>>)obj.get(menuName);
						// and loop over the items
						for(int i = 0; i < items.size(); i++) {
							// make sure it's valid sized!
							if(items.get(i).size() != 2) {
								UtilityBelt.displayErrorString("bad menu item: " + menuName + "." + (i+1));
								continue;
							}
							
							// parse the texture / display string
							String texture = items.get(i).get(0);
							
							UtilityButton button = new UtilityButton(texture, items.get(i).get(1));
							menu.addButton(button);
						}
						
						// now add the menu
						menus.put(menuName, menu);
						
						// set the root and current menus
						if(first) {
							first = false;
							Config.rootMenu = menuName;
							UtilityBelt.currentMenu = menuName;
						}
					}
					
					// enable the menu
					UtilityBelt.menuEnabled = true;
				}
				else {
					// error!
					UtilityBelt.displayErrorString("no root node!");
				}

				// and close up!
				input.close();
			}
			catch(Exception e) {
				UtilityBelt.displayErrorString("error parsing menu: " + e.getMessage());
			}
		}
		catch(Exception e) {
			UtilityBelt.displayErrorString(e.getMessage());
		}
		
		// and we're done
		return menus;
	}
}
