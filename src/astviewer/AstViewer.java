package astviewer;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.antlr.v4.gui.TreeViewer;

import antlr.GrammarParser.ProgramContext;
import dotgenerator.DotGenerator;

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

        JScrollPane scrollPane = new JScrollPane(viewer);
        scrollPane.setPreferredSize(new Dimension(800,600));

        JFrame frame = new JFrame("Antlr AST Viewer");
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void exportDot(String filePath) {
        DotGenerator dotGen = new DotGenerator();
        String dot = dotGen.toDot(tree);

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(dot);
            System.out.println("Arquivo dot salvo em: " + filePath);
        } catch (Exception e) {
            System.err.println("Erro ao salvar o arquivo dot: " + e.getMessage());
        }

    }
   
}
