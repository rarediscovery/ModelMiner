/*
 * ****************************************************
 * Filename :
 * Created  :
 * ****************************************************
 */
package com.rarediscovery.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author Temitope Ajagbe
 */
public class TemplateView extends JPanel {

    public TemplateView() {
        setLayout(new BorderLayout());
    }

    public TemplateView addFooter(JPanel footer) {
        add(footer, BorderLayout.SOUTH);
        return this;
    }

    public TemplateView addHeader(JPanel header) {
        add(header, BorderLayout.NORTH);
        return this;
    }

    public TemplateView addContent(JPanel content) {
        add(content, BorderLayout.CENTER);
        return this;
    }
    
    public TemplateView addLeftSideBar(JPanel sidebar) {
        //Wrap content in a panel with null layout
        JPanel noLayoutPanel = new JPanel();
        noLayoutPanel.add(sidebar);
        add(noLayoutPanel, BorderLayout.WEST);
        return this;
    }
    
    public TemplateView addRightSideBar(JPanel sidebar) {
        add(sidebar, BorderLayout.EAST);
        return this;
    }
}
