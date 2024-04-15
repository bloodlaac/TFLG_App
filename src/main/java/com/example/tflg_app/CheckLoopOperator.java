package com.example.tflg_app;

import java.util.LinkedList;
import java.util.List;

public class CheckLoopOperator {
    private static int logCounter = 0;
    private static int pos = 0;
    private static String input;
    private static Err err;
    private static int errPos;
    private static List<String> listOfIDs;
    private static List<String> listOfConst;

    private static void SetError(Err errorType, int errorPosition) {
        err = errorType;
        errPos = errorPosition;
    }

    public static Result Check(String inputString) {
        Reset();
        input = inputString;
        LoopOperator();
        return new Result(errPos, err, listOfIDs, listOfConst);
    }

    private static void Reset() {
        logCounter = 0;
        pos = 0;
        listOfIDs = new LinkedList<>();
        listOfConst = new LinkedList<>();
        SetError(Err.NoError, -1);
    }

    private static void LoopOperator() {
        States state = States.Start;
        StringBuilder id = new StringBuilder();
        StringBuilder constant = new StringBuilder();

        input = input.toUpperCase();
        input += '¶';

        while ((state != States.Error) && (state != States.Final)) {
            if (pos >= input.length()) {
                SetError(Err.OutOfRange, pos);
                state = States.Error;
            } else {
                switch (state) {
                    case Start -> {
                        if ((input.charAt(pos) == 'W')) {
                            state = States.WHL1;
                        } else {
                            SetError(Err.LetterExpected, pos);
                            state = States.Error;
                        }
                    }
                    case WHL1 -> {
                        if ((input.charAt(pos) == 'H')) {
                            state = States.WHL2;
                        } else {
                            SetError(Err.LetterExpected, pos);
                            state = States.Error;
                        }
                    }
                    case WHL2 -> {
                        if ((input.charAt(pos) == 'I')) {
                            state = States.WHL3;
                        } else {
                            SetError(Err.LetterExpected, pos);
                            state = States.Error;
                        }
                    }
                    case WHL3 -> {
                        if ((input.charAt(pos) == 'L')) {
                            state = States.WHL4;
                        } else {
                            SetError(Err.LetterExpected, pos);
                            state = States.Error;
                        }
                    }
                    case WHL4 -> {
                        if ((input.charAt(pos) == 'E')) {
                            state = States.WHL5;
                        } else {
                            SetError(Err.LetterExpected, pos);
                            state = States.Error;
                        }
                    }
                    case WHL5 -> {
                        if (input.charAt(pos) == '(') {
                            state = States.BRK;
                        } else if (!(Character.isWhitespace(input.charAt(pos)))) {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case BRK -> {
                        if (input.charAt(pos) == '0') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            listOfConst.add(constant.toString());
                            state = States.A1ZERO0;
                        } else if ((Character.isDigit(input.charAt(pos))) && (input.charAt(pos) != '0')) {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.A1DIGIT0;
                        } else if (input.charAt(pos) == '-') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.A1MINUS0;
                        } else if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            state = States.V1ID;
                            id = new StringBuilder(String.valueOf(input.charAt(pos)));
                        } else if (!(Character.isWhitespace(input.charAt(pos)))) {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case A1MINUS0 -> {
                        if (input.charAt(pos) == '0') {
                            constant.append(input.charAt(pos));
                            state = States.A1ZERO1;
                        } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.A1DIGIT0;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V1ID -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            } else if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (input.charAt(pos) == '[') {
                            listOfIDs.add(id.toString());
                            state = States.V1SQBR1;
                        } else if (input.charAt(pos) == ')') {
                            listOfIDs.add(id.toString());
                            state = States.BRK2;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.V1SPC3;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case V1SQBR2 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC6;
                        } else if (input.charAt(pos) == ')') {
                            state = States.BRK2;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case A1ZERO0 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC6;
                        } else if (input.charAt(pos) == '.') {
                            state = States.A1DOT;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case A1DIGIT0 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.A1DOT;
                        } else if (input.charAt(pos) == ')') {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.BRK2;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.SPC6;
                            }
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case A1ZERO1 -> {
                        if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.A1DOT;
                        } else {
                            SetError(Err.DotExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V1SPC3 -> {
                        if (input.charAt(pos) == '[') {
                            state = States.V1SQBR1;
                        } else if (input.charAt(pos) == ')') {
                            state = States.BRK2;
                        } else if (input.charAt(pos) == '=') {
                            state = States.REL1;
                        } else if (input.charAt(pos) == '<') {
                            state = States.REL2;
                        } else if (input.charAt(pos) == '!') {
                            state = States.REL3;
                        } else if (input.charAt(pos) == '>') {
                            state = States.REL4;
                        } else if (input.charAt(pos) == '&') {
                            state = States.LOG1;
                        } else if (input.charAt(pos) == '|') {
                            state = States.LOG2;
                        } else if (!(Character.isWhitespace(input.charAt(pos)))) {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case A1DOT -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            state = States.A1DIGIT1;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V1SQBR1 -> {
                        if (input.charAt(pos) == '0') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.V1ZERO;
                        } else if (input.charAt(pos) == '-') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.V1MINUS;
                        } else if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.V1DIGIT;
                        } else if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.V1ID2;
                        } else if (!(Character.isWhitespace(input.charAt(pos)))) {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case A1DIGIT1 -> {
                        if (input.charAt(pos) == 'E') {
                            constant.append(input.charAt(pos));
                            state = States.A1E;
                        } else if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (input.charAt(pos) == ')') {
                            listOfConst.add(constant.toString());
                            state = States.BRK2;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPC6;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case V1MINUS -> {
                        if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.V1DIGIT;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case A1E -> {
                        if (input.charAt(pos) == '-') {
                            constant.append(input.charAt(pos));
                            state = States.A1MINUS1;
                        } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != 0)) {
                            constant.append(input.charAt(pos));
                            state = States.A1DIGIT2;
                        } else {
                            SetError(Err.DigitMinusExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V1ZERO -> {
                        if (input.charAt(pos) == ']') {
                            listOfConst.add(constant.toString());
                            state = States.V1SQBR2;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPCV1;
                        } else {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V1DIGIT -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            }
                        } else if (input.charAt(pos) == ']') {
                            listOfConst.add(constant.toString());
                            state = States.V1SQBR2;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPCV1;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case V1ID2 -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (input.charAt(pos) == ']') {
                            listOfIDs.add(id.toString());
                            state = States.V1SQBR2;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.SPCV1;

                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case A1MINUS1 -> {
                        if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant.append(input.charAt(pos));
                            state = States.A1DIGIT2;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case SPCV1 -> {
                        if (input.charAt(pos) == ']') {
                            state = States.V1SQBR2;
                        } else if (!(Character.isWhitespace(input.charAt(pos)))) {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case A1DIGIT2 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPC6;
                        } else if (input.charAt(pos) == ')') {
                            listOfConst.add(constant.toString());
                            state = States.BRK2;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case SPC6 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '&') {
                                logCounter++;
                                if (logCounter > 6) {
                                    SetError(Err.OverflowExpressions, pos);
                                    state = States.Error;
                                } else {
                                    state = States.LOG1;
                                }
                            } else if (input.charAt(pos) == '|') {
                                logCounter++;
                                if (logCounter > 6) {
                                    SetError(Err.OverflowExpressions, pos);
                                    state = States.Error;
                                } else {
                                    state = States.LOG2;
                                }
                            } else if (input.charAt(pos) == '<') {
                                state = States.REL2;
                            } else if (input.charAt(pos) == '=') {
                                state = States.REL1;
                            } else if (input.charAt(pos) == '!') {
                                state = States.REL3;
                            } else if (input.charAt(pos) == '>') {
                                state = States.REL4;
                            } else if (input.charAt(pos) == ')') {
                                state = States.BRK2;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case REL2, REL4 -> {
                        if (input.charAt(pos) == '=') {
                            state = States.REL;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC7;
                        } else {
                            SetError(Err.EqualsExpected, pos);
                            state = States.Error;
                        }
                    }
                    case REL1, REL3 -> {
                        if (input.charAt(pos) == '=') {
                            state = States.REL;
                        } else {
                            SetError(Err.EqualsExpected, pos);
                            state = States.Error;
                        }
                    }
                    case LOG1, LOG3 -> {
                        if (input.charAt(pos) == '&') {
                            logCounter++;
                            if (logCounter > 6) {
                                SetError(Err.OverflowExpressions, pos);
                                state = States.Error;
                            } else {
                                state = States.LOG;
                            }
                        } else {
                            SetError(Err.AndExpected, pos);
                            state = States.Error;
                        }
                    }
                    case REL -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC7;
                        } else if (input.charAt(pos) == '0') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            listOfConst.add(constant.toString());
                            state = States.ZERO3;
                        } else if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.DIGIT4;
                        } else if (input.charAt(pos) == '-') {
                            constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.MINUS3;
                        } else if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id = new StringBuilder(String.valueOf(input.charAt(pos)));
                            state = States.ID3;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case LOG2, LOG4 -> {
                        if (input.charAt(pos) == '|') {
                            logCounter++;
                            if (logCounter > 6) {
                                SetError(Err.OverflowExpressions, pos);
                                state = States.Error;
                            } else {
                                state = States.LOG;
                            }
                        } else {
                            SetError(Err.OrExpected, pos);
                            state = States.Error;
                        }
                    }
                    // meow
                    case SPC7 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                listOfConst.add(constant.toString());
                                state = States.ZERO3;
                            } else if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.DIGIT4;
                            } else if (input.charAt(pos) == '-') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.MINUS3;
                            } else if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                                id = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.ID3;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case MINUS3 -> {
                        if (input.charAt(pos) == '0') {
                            constant.append(input.charAt(pos));
                            state = States.ZERO4;
                        } else if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant.append(input.charAt(pos));
                            state = States.DIGIT4;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case ID3 -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (input.charAt(pos) == '[') {
                            listOfIDs.add(id.toString());
                            state = States.SQBR3;
                        } else if (input.charAt(pos) == ')') {
                            listOfIDs.add(id.toString());
                            state = States.BRK2;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.SPC8;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case ZERO4 -> {
                        if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.DOT1;
                        } else {
                            SetError(Err.DotExpected, pos);
                            state = States.Error;
                        }
                    }
                    case SPC8 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '[') {
                                state = States.SQBR3;
                            } else if (input.charAt(pos) == ')') {
                                state = States.BRK2;
                            } else {
                                SetError(Err.BracketSpaceExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case DIGIT4 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.SPC11;
                            }
                        } else if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.DOT1;
                        } else if (input.charAt(pos) == ')') {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.BRK2;
                            }
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case DOT1 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            state = States.DIGIT5;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case SQBR3 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '-') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.MINUS2;
                            } else if (input.charAt(pos) == '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                listOfConst.add(constant.toString());
                                state = States.ZERO2;
                            } else if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.DIGIT3;
                            } else if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                                id = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.ID4;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case MINUS2 -> {
                        if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant.append(input.charAt(pos));
                            state = States.DIGIT3;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case DIGIT5 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPC11;
                        } else if (input.charAt(pos) == 'E') {
                            constant.append(input.charAt(pos));
                            state = States.E2;
                        } else if (input.charAt(pos) == ')') {
                            listOfConst.add(constant.toString());
                            state = States.BRK2;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case E2 -> {
                        if (input.charAt(pos) == '-') {
                            constant.append(input.charAt(pos));
                            state = States.MINUS4;
                        } else if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant.append(input.charAt(pos));
                            state = States.DIGIT6;
                        } else {
                            SetError(Err.DigitMinusExpected, pos);
                            state = States.Error;
                        }
                    }
                    case ID4 -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.SPC10;
                        } else if (input.charAt(pos) == ']') {
                            listOfIDs.add(id.toString());
                            state = States.SQBR4;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case DIGIT3 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPC10;
                        } else if (input.charAt(pos) == ']') {
                            listOfConst.add(constant.toString());
                            state = States.SQBR4;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case ZERO2 -> {
                        if (input.charAt(pos) == ']') {
                            state = States.SQBR4;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC10;
                        } else {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case MINUS4 -> {
                        if (Character.isDigit(input.charAt(pos)) && input.charAt(pos) != '0') {
                            constant.append(input.charAt(pos));
                            state = States.DIGIT6;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case SPC10 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == ']') {
                                state = States.SQBR4;
                            } else {
                                SetError(Err.BracketSpaceExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case SQBR4 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC11;
                        } else if (input.charAt(pos) == ')') {
                            state = States.BRK2;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case ZERO3 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.SPC11;
                        } else if (input.charAt(pos) == ')') {
                            state = States.BRK2;
                        } else if (input.charAt(pos) == '.') {
                            state = States.DOT1;
                        } else {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case DIGIT6 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.SPC11;
                        } else if (input.charAt(pos) == ')') {
                            listOfConst.add(constant.toString());
                            state = States.BRK2;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case SPC11 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '&') {
                                logCounter++;
                                if (logCounter > 6) {
                                    SetError(Err.OverflowExpressions, pos);
                                    state = States.Error;
                                } else {
                                    state = States.LOG3;
                                }
                            } else if (input.charAt(pos) == '|') {
                                logCounter++;
                                if (logCounter > 6) {
                                    SetError(Err.OverflowExpressions, pos);
                                    state = States.Error;
                                } else {
                                    state = States.LOG4;
                                }
                            } else if (input.charAt(pos) == ')') {
                                state = States.BRK2;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case LOG -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.BRK;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case BRK2 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '{') {
                                state = States.CURLY;
                            } else {
                                SetError(Err.BracketSpaceExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case CURLY -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                                id = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.V2ID;
                            } else {
                                SetError(Err.LetterExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case V2ID -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.V2SPC3;
                        } else if (input.charAt(pos) == '[') {
                            listOfIDs.add(id.toString());
                            state = States.V2SQBR1;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case V2SPC3 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '[') {
                                state = States.V2SQBR1;
                            } else if (input.charAt(pos) == '=') {
                                state = States.EQUAL;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case V2SQBR1 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '-') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.V2MINUS;
                            } else if (input.charAt(pos) == '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                listOfConst.add(constant.toString());
                                state = States.V2ZERO;
                            } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.V2DIGIT;
                            } else if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                                id = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.V2ID2;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case V2MINUS -> {
                        if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.V2DIGIT;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V2DIGIT -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.V2SPC5;
                        } else if (input.charAt(pos) == ']') {
                            listOfConst.add(constant.toString());
                            state = States.V2SQBR2;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case V2ZERO -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.V2SPC5;
                        } else if (input.charAt(pos) == ']') {
                            state = States.V2SQBR2;
                        } else {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V2ID2 -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.V2SPC5;
                        } else if (input.charAt(pos) == ']') {
                            listOfIDs.add(id.toString());
                            state = States.V2SQBR2;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case V2SPC5 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == ']') {
                                state = States.V2SQBR2;
                            } else {
                                SetError(Err.BracketSpaceExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case V2SQBR2 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.V2SPC6;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case V2SPC6 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '=') {
                                state = States.EQUAL;
                            } else {
                                SetError(Err.EqualsExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case EQUAL -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                                id = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3ID1;
                            } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3DIGIT1;
                            } else if (input.charAt(pos) == '-') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3MINUS1;
                            } else if (input.charAt(pos) == '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3ZERO1;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case T3MINUS1 -> {
                        if (input.charAt(pos) == '0') {
                            constant.append(input.charAt(pos));
                            state = States.T3ZERO2;
                        } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.T3DIGIT1;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3ZERO2 -> {
                        if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.T3DOT1;
                        } else {
                            SetError(Err.DotExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3DIGIT1 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.T3DOT1;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.T3SPC4;
                            }
                        } else if (input.charAt(pos) == ';') {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.SMCOL;
                            }
                        } else if ((input.charAt(pos) == '+') || (input.charAt(pos) == '-') || (input.charAt(pos) == '*') ||
                                (input.charAt(pos) == '/') || (input.charAt(pos) == '%')) {
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            } else {
                                listOfConst.add(constant.toString());
                                state = States.MATH;
                            }
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3ID1 -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (input.charAt(pos) == '[') {
                            listOfIDs.add(id.toString());
                            state = States.T3SQBR1;
                        } else if (input.charAt(pos) == ';') {
                            listOfIDs.add(id.toString());
                            state = States.SMCOL;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.T3SPC4;
                        } else if ((input.charAt(pos) == '+') || (input.charAt(pos) == '-') || (input.charAt(pos) == '*') ||
                                (input.charAt(pos) == '/') || (input.charAt(pos) == '%')) {
                            listOfIDs.add(id.toString());
                            state = States.MATH;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3DOT1 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            state = States.T3DIGIT2;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3SQBR1 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (Character.isLetter(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                                id = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3ID2;
                            } else if (input.charAt(pos) == '-') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3MINUS3;
                            } else if (input.charAt(pos) == '0') {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                listOfConst.add(constant.toString());
                                state = States.T3ZERO3;
                            } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                                constant = new StringBuilder(String.valueOf(input.charAt(pos)));
                                state = States.T3DIGIT4;
                            } else {
                                SetError(Err.UnexpectedSymbolException, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case T3MINUS3 -> {
                        if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.T3DIGIT4;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3DIGIT2 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (input.charAt(pos) == 'E') {
                            constant.append(input.charAt(pos));
                            state = States.T3E1;
                        } else if (input.charAt(pos) == ';') {
                            listOfConst.add(constant.toString());
                            state = States.SMCOL;
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.T3SPC4;
                        } else if ((input.charAt(pos) == '+') || (input.charAt(pos) == '-') || (input.charAt(pos) == '*') ||
                                (input.charAt(pos) == '/') || (input.charAt(pos) == '%')) {
                            listOfConst.add(constant.toString());
                            state = States.MATH;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3ID2 -> {
                        if (Character.isLetterOrDigit(input.charAt(pos)) || (input.charAt(pos) == '_')) {
                            id.append(input.charAt(pos));
                            if ((id.toString().equals("FOR")) || (id.toString().equals("WHILE")) || (id.toString().equals("BREAK"))
                                    || (id.toString().equals("SWITCH")) || (id.toString().equals("CASE")) || (id.toString().equals("CONST"))) {
                                SetError(Err.ReservedWordException, pos);
                                state = States.Error;
                            }
                            if (id.length() > 12) {
                                SetError(Err.OverflowID, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfIDs.add(id.toString());
                            state = States.T3SPC3;
                        } else if (input.charAt(pos) == ']') {
                            listOfIDs.add(id.toString());
                            state = States.T3SQBR2;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3DIGIT4 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                            if (!((constant.length() < 6) && (Integer.parseInt(constant.toString()) >= -32768) &&
                                    (Integer.parseInt(constant.toString()) <= 32767))) {
                                SetError(Err.IntBoundsException, pos);
                                state = States.Error;
                            }
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.T3SPC3;
                        } else if (input.charAt(pos) == ']') {
                            listOfConst.add(constant.toString());
                            state = States.T3SQBR2;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3ZERO3 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.T3SPC3;
                        } else if (input.charAt(pos) == ']') {
                            state = States.T3SQBR2;
                        } else {
                            SetError(Err.BracketSpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3E1 -> {
                        if (input.charAt(pos) == '-') {
                            constant.append(input.charAt(pos));
                            state = States.T3MINUS2;
                        } else if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.T3DIGIT3;
                        } else {
                            SetError(Err.DigitMinusExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3SPC3 -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == ']') {
                                state = States.T3SQBR2;
                            } else {
                                SetError(Err.BracketSpaceExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case T3MINUS2 -> {
                        if (Character.isDigit(input.charAt(pos)) && (input.charAt(pos) != '0')) {
                            constant.append(input.charAt(pos));
                            state = States.T3DIGIT3;
                        } else {
                            SetError(Err.DigitExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3SQBR2 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.T3SPC4;
                        } else if (input.charAt(pos) == ';') {
                            state = States.SMCOL;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case T3DIGIT3 -> {
                        if (Character.isDigit(input.charAt(pos))) {
                            constant.append(input.charAt(pos));
                        } else if (Character.isWhitespace(input.charAt(pos))) {
                            listOfConst.add(constant.toString());
                            state = States.T3SPC4;
                        } else if (input.charAt(pos) == ';') {
                            listOfConst.add(constant.toString());
                            state = States.SMCOL;
                        } else if ((input.charAt(pos) == '+') || (input.charAt(pos) == '-') || (input.charAt(pos) == '*') ||
                                (input.charAt(pos) == '/') || (input.charAt(pos) == '%')) {
                            listOfConst.add(constant.toString());
                            state = States.MATH;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3ZERO1 -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.T3SPC4;
                        } else if ((input.charAt(pos) == '+') || (input.charAt(pos) == '-') || (input.charAt(pos) == '*') ||
                                (input.charAt(pos) == '/') || (input.charAt(pos) == '%')) {
                            state = States.MATH;
                        } else if (input.charAt(pos) == ';') {
                            state = States.SMCOL;
                        } else if (input.charAt(pos) == '.') {
                            constant.append(input.charAt(pos));
                            state = States.T3DOT1;
                        } else {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case T3SPC4 -> {
                        if (input.charAt(pos) == ';') {
                            state = States.SMCOL;
                        } else if ((input.charAt(pos) == '+') || (input.charAt(pos) == '-') || (input.charAt(pos) == '*') ||
                                (input.charAt(pos) == '/') || (input.charAt(pos) == '%')) {
                            state = States.MATH;
                        } else if (!(Character.isWhitespace(input.charAt(pos)))) {
                            SetError(Err.UnexpectedSymbolException, pos);
                            state = States.Error;
                        }
                    }
                    case SMCOL -> {
                        if (!Character.isWhitespace(input.charAt(pos))) {
                            if (input.charAt(pos) == '}') {
                                state = States.CURLY2;
                            } else {
                                SetError(Err.BracketSpaceExpected, pos);
                                state = States.Error;
                            }
                        }
                    }
                    case MATH -> {
                        if (Character.isWhitespace(input.charAt(pos))) {
                            state = States.EQUAL;
                        } else {
                            SetError(Err.SpaceExpected, pos);
                            state = States.Error;
                        }
                    }
                    case CURLY2 -> state = States.Final;
                }
            }
            pos++;
        }
    }
}
