package eastangliamapclient.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONParser
{
    public static Object parseJSON(String json)
    {
        json = json.trim();

        if (json.startsWith("\""))
            return valueOf(json.substring(1, indexOfNextUnescapedChar('"', json)), json);
        else if (json.startsWith("["))
            return getArray(json);

      //else if (json.startsWith("{"))
            return getObject(json);
    }

    private static Object valueOf(String string, String in)
    {
        Object value = null;
        int charPos = in.indexOf("\"" + string + "\":") + string.length() + 3;

        if (in.startsWith("null", charPos))
            value = null;
        else if (in.charAt(charPos) == '"')
            value = in.substring(charPos + 1, in.indexOf("\"", charPos + 1));
        else if (in.charAt(charPos) == '{')
            value = getObject(in.substring(charPos));
        else if (in.charAt(charPos) == '[')
            value = getArray(in.substring(charPos));
        else if (in.startsWith("true", charPos))
            value = true;
        else if (in.startsWith("false", charPos))
            value = false;
        else
            value = getNumber(in.substring(charPos));

        return value;
    }

    private static Map<String, Object> getObject(String string)
    {
        String objString = "";
        int noArrays = 0;
        int index = 0;
        char lastChar = ' ';

        // Gets NEXT object, disposes of further objects
        for (char chr : string.toCharArray())
        {
            index++;

            if (lastChar != '\\' || index == 1)
            {
                if (chr == '{')
                    noArrays++;
                else if (chr == '}')
                    noArrays--;
            }

            if (noArrays == 0)
            {
                objString = string.substring(0, index);
                break;
            }
            lastChar = chr;
        }

        Map<String, Object> map = new HashMap<>();
        List<String> pairs = new ArrayList<>();

        int inArr = 0, inObj = 0;
        boolean inStr = false;
        index = 0;
        String s = objString.substring(1, objString.length() - 1);
        for (char chr : s.toCharArray())
        {
            index++;

            if (lastChar != '\\' || index == 1)
            {
                if (chr == '"')
                    inStr = !inStr;
                else if (chr == '{')
                    inObj++;
                else if (chr == '}')
                    inObj--;
                else if (chr == '[')
                    inArr++;
                else if (chr == ']')
                    inArr--;
            }

            if ((chr == ',' || index == s.length()) && !inStr && inObj == 0 && inArr == 0)
            {
                pairs.add(s.substring(0, (index == s.length() ? index : index - 1)));
                s = s.substring(index);
                index = 0;
            }
            lastChar = chr;
        }

        for (String pair : pairs)
        {
            int strEnd = pair.indexOf("\"", 1);
            String key = pair.substring(1, strEnd);
            String val = pair.substring(strEnd + 2);

            if (val.charAt(0) == '"')
                map.put(key, val.substring(1, val.length() - 1));
            else
                map.put(key, valueOf(key, pair));
        }

        return map;
    }

    private static List<Object> getArray(String string)
    {
        String arrString = "";
        int inArr = 0;
        int index = 0;
        boolean escaped = false;

        for (char chr : string.toCharArray())
        {
            index++;

            if (!escaped)
                if (chr == '[')
                    inArr++;
                else if (chr == ']')
                    inArr--;
            if (chr == '\\')
                escaped = !escaped;

            if (inArr == 0)
            {
                arrString = string.substring(0, index);
                break;
            }
        }

        List<Object> list = new ArrayList<>();
        List<String> pairs = new ArrayList<>();

        inArr = 0;
        int inObj = 0;
        boolean inStr = false;
        index = 0;
        String s = arrString.substring(1, arrString.length() - 1);
        escaped = false;
        for (char chr : s.toCharArray())
        {
            index++;

            if (!escaped)
                if (chr == '"')
                    inStr = !inStr;
                else if (!inStr && chr == '{')
                    inObj++;
                else if (!inStr && chr == '}')
                    inObj--;
                else if (!inStr && chr == '[')
                    inArr++;
                else if (!inStr && chr == ']')
                    inArr--;

            if (chr == '\\')
                escaped = !escaped;

            if ((chr == ',' || index == s.length()) && !inStr && inObj == 0 && inArr == 0)
            {
                pairs.add(s.substring(0, (index == s.length() ? index : index - 1)));
                s = s.substring(index);
                index = 0;
            }
        }

        for (String pair : pairs)
        {
            if (pair.charAt(0) == '"')
                list.add(pair.substring(1, pair.length() - 1));
            else if (pair.charAt(0) == '{')
                list.add(getObject(pair));
            else if (pair.charAt(0) == '[')
                list.add(getArray(pair));
        }

        return list;
    }

    private static Object getNumber(String string)
    {
        Object num = Double.parseDouble(string);
        long num2 = Long.MIN_VALUE;

        try { num2 = Long.parseLong(string); }
        catch (NumberFormatException e) { /* thrown if string contains (negative?) standard form */ }

        if (num2 != Long.MIN_VALUE && ((double) num) == num2)
            num = num2;

        return num;
    }

    private static int indexOfNextUnescapedChar(char toFind, String str)
    {
        char[] strChars = str.toCharArray();
        boolean isEscaped = false;
        int index = -1;

        for (char strChar : strChars)
        {
            index++;

            if (!isEscaped && strChar == toFind)
                return index;

            if (strChar == '\\')
                isEscaped = !isEscaped;
        }

        return -1;
    }
}