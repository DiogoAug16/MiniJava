package com.minijava.generateflags;

import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;

import com.minijava.interpreter.Interpreter;
import com.minijava.llvm.LLVMGenerator;
import com.minijava.tac.TACGenerator;
import com.minijava.tac.TACInstruction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MainHelper {

    public static void generateTacAndLlvm(String[] args, ParseTree tree) {
        if (args.length > 0 && args[0].equals("--gerar-tac")) {
            TACGenerator tacGen = new TACGenerator();
            tacGen.visit(tree);

            File tacDir = new File("output/tac");
            if (!tacDir.exists() && !tacDir.mkdirs()) {
                System.err.println("[Erro] Não foi possível criar o diretório output/tac.");
                return;
            }

            try (PrintWriter writer = new PrintWriter("output/tac/programa_fonte.tac")) {
                for (TACInstruction instr : tacGen.getInstructions()) {
                    writer.println(instr.toString());
                }
                System.out.println("Código intermediário TAC gerado em: output/tac/programa_fonte.tac");
            } catch (IOException e) {
                System.err.println("[Erro na geração do código intermediário] " + e.getMessage());
                return;
            }

            if (args.length > 1 && args[1].equals("--gerar-llvm")) {
                List<TACInstruction> tacInstructions = tacGen.getInstructions();
                List<String> tacStrings = tacInstructions.stream()
                                                        .map(Object::toString)
                                                        .collect(Collectors.toList());

                LLVMGenerator llvmGenerator = new LLVMGenerator(tacStrings);
                String llvmCode = llvmGenerator.generate();

                File llvmDir = new File("output/llvm");
                if (!llvmDir.exists() && !llvmDir.mkdirs()) {
                    System.err.println("[Erro] Não foi possível criar o diretório output/llvm.");
                    return;
                }

                File runtimeDir = new File("output/runtime");
                if (!runtimeDir.exists() && !runtimeDir.mkdirs()) {
                    System.err.println("[Erro] Não foi possível criar o diretório output/runtime.");
                    return;
                }

                File programaDir = new File("output/programa");
                if (!programaDir.exists() && !programaDir.mkdirs()) {
                    System.err.println("[Erro] Não foi possível criar o diretório output/programa.");
                    return;
                }

                try (PrintWriter writer = new PrintWriter("output/llvm/programa_fonte.ll")) {
                    writer.print(llvmCode);
                    System.out.println("Código LLVM gerado em: output/llvm/programa_fonte.ll");
                } catch (IOException e) {
                    System.err.println("[Erro na geração do código LLVM] " + e.getMessage());
                    return;
                }

                try {
                    if (!runCommand("clang", "-c", "runtime/concat.c", "-o", "output/runtime/concat.o")) {
                        System.err.println("[Erro] Falha ao compilar concat.c");
                        return;
                    }

                    if (!runCommand("clang", "-c", "runtime/itoa.c", "-o", "output/runtime/itoa.o")) {
                        System.err.println("[Erro] Falha ao compilar itoa.c");
                        return;
                    }

                    if (!runCommand("clang",
                            "-Wno-override-module",
                            "output/llvm/programa_fonte.ll",
                            "output/runtime/concat.o",
                            "output/runtime/itoa.o",
                            "-o",
                            "output/programa/programa_fonte.exe",
                            "-llegacy_stdio_definitions")) {
                        System.err.println("[Erro] Falha ao linkar e gerar o executável final.");
                        return;
                    }

                    // Para outros sistemas operacionais
                    if (!runCommand("clang",
                            "-Wno-override-module",
                            "output/llvm/programa_fonte.ll",
                            "output/runtime/concat.o",
                            "output/runtime/itoa.o",
                            "-o",
                            "output/programa/programa_fonte",
                            "-llegacy_stdio_definitions")) {
                        System.err.println("[Erro] Falha ao linkar e gerar o executável final.");
                        return;
                    }
                    
                    System.out.println("Programa executável gerado em: output/programa/programa_fonte");

                } catch (IOException | InterruptedException e) {
                    System.err.println("[Erro ao executar comandos clang] " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            Interpreter interpreter = new Interpreter();
            interpreter.visit(tree);
        }
    }

    private static boolean runCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = pb.start();
        int exitCode = p.waitFor();
        return exitCode == 0;
    }
}
