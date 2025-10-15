package org.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.beryx.textio.InputReader.ValueChecker;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.console.ConsoleTextTerminal;

public class App {

    public record SymbolRecord(Boolean single) {}

    private HashMap<String, SymbolRecord> SYMBOLS_MAP = new HashMap<>() {
        {
            put("*", new SymbolRecord(true));
            put("+", new SymbolRecord(true));
            put("-", new SymbolRecord(true));
            put("/", new SymbolRecord(true));
            put("^", new SymbolRecord(true));
            put("root", new SymbolRecord(false));
            put("ceil", new SymbolRecord(false));
            put("floor", new SymbolRecord(false));
        }
    };
    // private String[] SYMBOLS = { "*", "+", "-", "/", "^", "root", "ceil", "floor" };
    private TextIO io;
    private TextTerminal<?> terminal;

    App(String[] args) {
        this.io = TextIoFactory.getTextIO();
        this.terminal = io.getTextTerminal();
    }

    public static void main(String[] argv) {
        System.setProperty("java.awt.headless", "true");
        App app = new App(argv);
        while (true) {
            app.resolve();
        }
    }

    private void resolve() {
        String symbol = read_symbol();
        String nums = read_numbers(symbol);

        String[] parts = nums.split(",");
        for (String part : parts) {
            part.trim();
        }

        String first = parts[0];
        String second = new String();

        if (parts.length > 1 && !parts[1].isBlank()) {
            second = parts[1];
        }

        Double first_num = Double.parseDouble(first);
        Double second_num = Double.parseDouble("0");
        if (!second.isBlank()) {
            second_num = Double.parseDouble(second);
        }

        switch (symbol) {
            case "*":
                this.terminal.println("Result:" + (first_num * second_num));
                break;
            case "+":
                this.terminal.println("Result:" + (first_num + second_num));
                break;
            case "-":
                this.terminal.println("Result:" + (first_num - second_num));
                break;
            case "/":
                this.terminal.println("Result:" + (first_num / second_num));
                break;
            case "^":
                this.terminal.println(
                    "Result" + (Math.pow(first_num, second_num))
                );
                break;
            case "root":
                this.terminal.println("Result" + Math.sqrt(first_num));
                break;
            case "ceil":
                this.terminal.println("Result" + Math.ceil(first_num));
                break;
            case "floor":
                this.terminal.println("Result" + Math.floor(first_num));
                break;
        }
        this.terminal.println("\n");
    }

    private boolean get_double_parse(String symbol) {
        boolean double_parse = true;
        for (Map.Entry<String, SymbolRecord> entry : SYMBOLS_MAP.entrySet()) {
            String key = entry.getKey();
            SymbolRecord value = entry.getValue();

            if (!key.equals(symbol)) {
                continue;
            }

            if (value.equals(new SymbolRecord(false))) {
                double_parse = false;
                break;
            }
        }

        return double_parse;
    }

    private String read_symbol() {
        List<String> symbols = new ArrayList<>(SYMBOLS_MAP.keySet());
        String symbol = this.io.newStringInputReader()
            .withPossibleValues(symbols)
            .read("Please enter the operation");
        return symbol;
    }

    private String read_numbers(String symbol) {
        String nums = this.io.newStringInputReader()
            .withValueChecker(
                new ValueChecker<String>() {
                    @Override
                    public List<String> getErrorMessages(
                        String val,
                        String itemName
                    ) {
                        List<String> errs = new ArrayList<String>();

                        String[] parts = val.split(",");

                        boolean double_parse = get_double_parse(symbol);
                        if (parts.length != 2 && (double_parse)) {
                            errs.add("Please ensure you have exactly 2 values");
                            return errs;
                        }

                        String first = parts[0].trim();
                        String second = new String();

                        if (double_parse) {
                            second = parts[1].trim();
                        }

                        try {
                            Double.parseDouble(first);
                            if (!second.isBlank()) {
                                Double.parseDouble(second);
                            }
                        } catch (NumberFormatException e) {
                            errs.add("Must contain valid numbers " + e);
                        }

                        return errs;
                    }
                }
            )
            .read("Please enter your number(s) separated by a comma");

        return nums;
    }
}
