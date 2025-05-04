package dotgenerator;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.concurrent.atomic.AtomicInteger;

public class DotGenerator {

    private final StringBuilder builder = new StringBuilder();
    private final AtomicInteger nodeCount = new AtomicInteger(0);

    public String toDot(ParseTree tree) {
        builder.append("digraph dotAst {\n");
        builder.append("  node [shape=box, style=filled, fillcolor=lightgray];\n");
        walk(tree, -1);
        builder.append("}\n");
        return builder.toString();
    }

    private void walk(ParseTree node, int parentId) {
        int currentId = nodeCount.getAndIncrement();

        String label = escapeLabel(getNodeText(node));
        builder.append(String.format("  node%d [label=\"%s\"];\n", currentId, label));

        if (parentId >= 0) {
            builder.append(String.format("  node%d -> node%d;\n", parentId, currentId));
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            walk(node.getChild(i), currentId);
        }
    }

    private String getNodeText(ParseTree node) {
        if (node instanceof TerminalNode) {
            return node.getText();
        } else {
            return node.getClass().getSimpleName().replace("Context", "");
        }
    }

    private String escapeLabel(String label) {
        return label.replace("\"", "\\\"");
    }
}
