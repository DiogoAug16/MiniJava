package com.minijava.ast.viewer;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.antlr.v4.gui.TreeViewer;

import com.minijava.antlr.MiniJavaParser.ProgramContext;

public class AstViewer {
    private ProgramContext tree;
    private List<String> ruleNames;

    public AstViewer(ProgramContext tree, List<String> ruleNames) {
        this.tree = tree;
        this.ruleNames = ruleNames;
    }

    public void show() {
        TreeViewer viewer = new TreeViewer(ruleNames, tree);
        viewer.setScale(1.5f);

        viewer.addMouseWheelListener(new MouseWheelListener() {
            float scale = 1.5f;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        scale += 0.1f;
                    } else if (scale > 0.2f ) {
                        scale -= 0.1f;
                    }
                    viewer.setScale(scale);
                    viewer.revalidate();
                    viewer.repaint();

                }
            }

        });


        JFrame frame = new JFrame("Antlr AST Viewer");
        JScrollPane scrollPane = new JScrollPane(viewer);
        scrollPane.setPreferredSize(new Dimension(800,600));
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
   
}
