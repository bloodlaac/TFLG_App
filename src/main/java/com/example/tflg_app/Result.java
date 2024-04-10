package com.example.tflg_app;

import com.example.tflg_app.Err;

import java.util.List;
import java.util.Objects;

public class Result {
    private final int errPosition;
    private final Err err;
    private final List<String> listOfIDs;
    private final List<String> listOfConst;

    public Result(int errPosition, Err err, List<String> listOfIDs, List<String> listOfConst) {
        this.errPosition = errPosition;
        this.err = err;
        this.listOfIDs = listOfIDs;
        this.listOfConst = listOfConst;
    }

    public int getErrPosition() {
        return errPosition;
    }

    public List<String> getListOfIDs() {
        return listOfIDs;
    }

    public List<String> getListOfConst() {
        return listOfConst;
    }

    public String getErr() {
        switch (err) {
            case NoError -> {
                return "Нет ошибок.";
            }
            case OutOfRange -> {
                return "Выход за границы анализируемой строки.";
            }
            case LetterExpected -> {
                return "Ожидается буква.";
            }
            case LetterDigitExpected -> {
                return "Ожидается буква или цифра.";
            }
            case DigitExpected -> {
                return "Ожидается цифра.";
            }
            case SpaceExpected -> {
                return "Ожидается пробел";
            }
            case DotExpected -> {
                return "Ожидается точка";
            }
            case DigitMinusExpected -> {
                return "Ожидается цифра или минус";
            }
            case BracketSpaceExpected -> {
                return "Ожидается кавычка или пробел";
            }
            case EqualsExpected -> {
                return "Ожидается =";
            }
            case OrExpected -> {
                return "Ожидается |";
            }
            case AndExpected -> {
                return "Ожидается &";
            }
            case UnexpectedSymbolException -> {
                return "Недопустимый символ";
            }
            case OverflowID -> {
                return "Недопустимая длина идентификатора.";
            }
            case IntBoundsException -> {
                return "Недопустимое значение целого числа.";
            }
            case ReservedWordException -> {
                return "ID не должен быть зарезервированным словом.";
            }
            case OverflowExpressions -> {
                return "Допустимое количество простых выражений превышено.";
            }
            case UnrecognizedError -> {
                return "Неизвестная ошибка.";
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result result)) return false;

        if (getErrPosition() != result.getErrPosition()) return false;
        if (!Objects.equals(getErr(), result.getErr())) return false;
        if (!getListOfIDs().equals(result.getListOfIDs())) return false;
        return getListOfConst().equals(result.getListOfConst());
    }

    @Override
    public int hashCode() {
        int result = getErrPosition();
        result = 31 * result + getErr().hashCode();
        result = 31 * result + getListOfIDs().hashCode();
        result = 31 * result + getListOfConst().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "errPosition=" + errPosition +
                ", err=" + err +
                ", listOfIDs=" + listOfIDs +
                ", listOfConst=" + listOfConst +
                '}';
    }
}