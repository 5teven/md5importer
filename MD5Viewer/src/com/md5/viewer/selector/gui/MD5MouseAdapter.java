package com.md5.viewer.selector.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class MD5MouseAdapter extends MouseAdapter {
	
	public void mouseClicked(MouseEvent event)
	{
		JButton button = (JButton)event.getSource();
		String name = button.getName();
		System.out.println(name);
		
		if(name.equals("btnHierarchyLoad"))
		{
			// TODO
		}
		else if (name.equals("btnBaseAnimLoad"))
		{
			// todo
		}
		else if(name.equals("btnAdd"))
		{
			// TODO
		}
		else if(name.endsWith("btnRemove"))
		{
			// TODO
		}
		else if(name.equals("btnOK"))
		{
			// TODO
		}
		else if(name.equals("btnCancel"))
		{
			// TODO
		}
		else
		{
			System.out.println("Que?");
		}
	}
}
