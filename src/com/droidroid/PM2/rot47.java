package com.droidroid.PM2;

/**
 * Created by ed on 20/08/14.
 */
public class rot47 {

    public String decode(String value)
    {
        int length = value.length();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            char c = value.charAt(i);
            if (c != ' ')
            {
                c += 47;
                if (c > '~')
                    c -= 94;
            }
            result.append(c);
        }
        return result.toString();
    }
}
