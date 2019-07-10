package com.dhwaniris.dynamicForm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        assertEquals("5.00", get3DecimalOrder("5",3));
    }


    String get3DecimalOrder(String order, int decimalCount) {
        try {
            return String.format("%." + decimalCount + "f", Float.parseFloat(order));

        } catch (Exception e) {
            return String.format("%." + decimalCount + "f", Float.parseFloat(order));

        }
    }

    public String getDecimalOrder(String parentOrder, String childOrder, int decimalCount) {
        int childOrderInt = Integer.parseInt(childOrder);
        String newOrder = parentOrder + "." + childOrder;
        String decimalPlaces = String.format("%0" + decimalCount + "d", childOrderInt);
        if (childOrderInt % 10 == 0) {
            newOrder = parentOrder + "." + decimalPlaces.substring(0, decimalPlaces.length() - 1);
        } else {
            newOrder = parentOrder + "." + decimalPlaces;
        }
        return newOrder;
    }

    private String getActualOrder(String parentOrder, int totalNumberOfQuestion, int currentNestedOrder, int maxDecimal) {
        int actualOrder = currentNestedOrder % totalNumberOfQuestion;
        actualOrder = actualOrder == 0 ? currentNestedOrder : actualOrder;
        String actualStringOrder = null;

        if (maxDecimal == 3) {
            if (String.valueOf(actualOrder).length() == 1) {
                actualStringOrder = parentOrder + "." + "00" + actualOrder;
            } else if (String.valueOf(actualOrder).length() == 2) {
                actualStringOrder = parentOrder + "." + "0" + actualOrder;
            } else {
                actualStringOrder = parentOrder + "." + "" + actualOrder;
            }

        } else if (maxDecimal == 2) {
            if (String.valueOf(actualOrder).length() == 1) {
                actualStringOrder = parentOrder + "." + "0" + actualOrder;
            } else if (String.valueOf(actualOrder).length() == 2) {
                actualStringOrder = parentOrder + "." + actualOrder;
            }

        } else {
            if (String.valueOf(actualOrder).length() == 1) {
                actualStringOrder = parentOrder + "." + "" + actualOrder;
            }

        }
        return actualStringOrder;

    }
}