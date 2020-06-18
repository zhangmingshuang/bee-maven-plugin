package com.nascent.maven.plugin.bee.utils;

import java.util.Arrays;

/**
 * String工具类.
 *
 * @author zhangmsh
 */
@SuppressWarnings({"java:S3358", "java:S127", "java:S125"})
public final class StringUtils {

    private StringUtils() {
        //private
    }

    /**
     * 返回一个重复之后的字符串.
     * <pre>
     * 例：
     * repat("hai",3)
     * 返回haihaihai
     * </pre>
     *
     * @param str    字符串，如果为null则为"null"
     * @param repeat 重复次数 =0将返回"",=1 将返回原始字符串
     * @return
     */
    public static String repeat(String str, int repeat) {
        int len = 0;
        if (str == null || (len = str.length()) == 0) {
            return str;
        }
        if (repeat <= 1) {
            return repeat == 0 ? "" : str;
        }
        //计算需要的数组长度
        long newSize = (long) len * (long) repeat;
        int size = (int) newSize;
        if (size != newSize) {
            //超出Int的最大值
            throw new ArrayIndexOutOfBoundsException("Required array size too large: " + newSize);
        }
        char[] array = new char[size];
        //将原始字符串转换成char数组到array中
        str.getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }

    /**
     * 返回一个重复之后的字符串.
     *
     * <pre>
     * 例：
     * repat("hai",3,",")
     * 返回hai,hai,hai
     * </pre>
     *
     * @param str    字符串，如果为null则为"null"
     * @param repeat 重复次数 =0将返回"",=1 将返回原始字符串
     * @param split  重复字符串中间的分割符
     * @return 处理后的字符串
     */
    public static String repeat(String str, int repeat, CharSequence split) {
        int len = 0;
        if (str == null || (len = str.length()) == 0) {
            return str;
        }
        if (repeat <= 1) {
            return repeat == 0 ? "" : str;
        }
        str = new StringBuilder(str).append(split).toString();
        //计算需要的数组长度
        long newSize = (long) len * (long) repeat;
        int size = (int) newSize;
        if (size != newSize) {
            //超出Int的最大值
            throw new ArrayIndexOutOfBoundsException("Required array size too large: " + newSize);
        }
        char[] array = new char[size];
        //将原始字符串转换成char数组到array中
        str.getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(Arrays.copyOf(array, size - split.length()));
    }

    /**
     * 判断char是否为Surrogate区的字符.
     *
     * @param string 字符串
     * @param index  索引
     * @return 如果是返回true，索引index如果超出返回false
     */
    public static boolean validSurrogatePairAt(CharSequence string, int index) {
        return index >= 0
            && index <= (string.length() - 2)
            && Character.isHighSurrogate(string.charAt(index))
            && Character.isLowSurrogate(string.charAt(index + 1));
    }

    /**
     * 取得二个字符串的相同前缀.
     * <pre>
     * 例：
     * String c = commonPrefix("hello","he");  返回he
     * String c = commonPrefix("hello","her"); 返回he
     * </pre>
     *
     * @param a 字符串1  如果为null则返回""
     * @param b 字符串2 如果为null则返回""
     * @return 相同前缀字符串
     */
    public static String commonPrefix(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            return "";
        }
        int maxPrefixLength = Math.min(a.length(), b.length());
        int p = 0;
        while (p < maxPrefixLength && a.charAt(p) == b.charAt(p)) {
            p++;
        }
        if (validSurrogatePairAt(a, p - 1) || validSurrogatePairAt(b, p - 1)) {
            p--;
        }
        return a.subSequence(0, p).toString();
    }

    /**
     * 取得二个字符串的相同后缀.
     * <pre>
     * 例：
     * String c = commonSuffix("hello","lo");  返回lo
     * String c = commonSuffix("hello","nlo"); 返回lo
     * </pre>
     *
     * @param a 字符串1  如果为null则返回""
     * @param b 字符串2 如果为null则返回""
     * @return 相同后缀字符串
     */
    public static String commonSuffix(CharSequence a, CharSequence b) {
        int maxSuffixLength = Math.min(a.length(), b.length());
        int s = 0;
        while (s < maxSuffixLength && a.charAt(a.length() - s - 1) == b
            .charAt(b.length() - s - 1)) {
            s++;
        }
        if (validSurrogatePairAt(a, a.length() - s - 1)
            || validSurrogatePairAt(b, b.length() - s - 1)) {
            s--;
        }
        return a.subSequence(a.length() - s, a.length()).toString();
    }

    /**
     * 验证字符串是否为""或者null.
     * <pre>
     * 例：
     * isEmpty(null) = true
     * isEmpty("") = true
     * isEmpty(" ") = false
     * isEmpty("hai") = false
     * isEmpty(" hai ") = false
     * </pre>
     *
     * @param cs 要验证的字符串
     * @return 如果字符串为""或者null返回true
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String getDefault(String cs, String def) {
        return cs == null || cs.length() == 0 ? def : cs;
    }

    /**
     * 验证字符串数组之中是否有 "" 或 null.
     * <pre>
     * 例：
     * isAnyEmpty(null) = true
     * isAnyEmpty(null,"hai") = true
     * isAnyEmpty("","hai") = true
     * isAnyEmpty("hai","") = true
     * isAnyEmpty(" hai ",null) = true
     * isAnyEmpty(" ","hai") = false
     * isAnyEmpty("hai","hello") = false
     * </pre>
     *
     * @param css 要验证的字符串数组
     * @return 如果字符串数组中有字符串为""或者null返回true
     */
    public static boolean isAnyEmpty(CharSequence... css) {
        int len = 0;
        if (css == null || (len = css.length) == 0) {
            return true;
        }
        CharSequence cs = null;
        for (int i = 0; i < len; ++i) {
            cs = css[i];
            if (cs == null || cs.length() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证字符串是否为： " "(空格) 或 "" 或  null.
     * <pre>
     * 例：
     * isBlank(null) = true
     * isBlank("") = true
     * isBlank(" ") = true
     * isBlank("hai") = false
     * isBlank(" hai ") = false
     * </pre>
     *
     * @param cs 要验证的字符串
     * @return 如果字符串是 " " 或者 "" 或者 null 返回true
     */
    public static boolean isBlank(CharSequence cs) {
        int len = 0;
        if (cs == null || (len = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证字符串数组中的字符串是否为 ：" "(空格) 或 "" 或 null.
     * <pre>
     * 例：
     * isNoneBlank(null)             = false
     * isNoneBlank(null, "hai")      = false
     * isNoneBlank(null, null)       = false
     * isNoneBlank("", "hai")        = false
     * isNoneBlank("hai", "")        = false
     * isNoneBlank("  hai  ", null)  = false
     * isNoneBlank(" ", "hai")       = false
     * isNoneBlank("hai", "hello")   = true
     * </pre>
     *
     * @param css 字符序列
     * @return 是否有null或者空白的字符序列
     */
    public static boolean isAnyBlank(CharSequence... css) {
        int len = 0;
        if (css == null || (len = css.length) == 0) {
            return true;
        }
        for (int i = 0; i < len; ++i) {
            if (isBlank(css[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串去左右空格.
     * <pre>
     * 例：
     * trimToNull(null)          = null
     * trimToNull("")            = null
     * trimToNull("     ")       = null
     * trimToNull("abc")         = "abc"
     * trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str 要操作的字符串
     * @return 如果字符串为""或者" "或者null返回null，否则返回去除左右空格之后的字符串
     */
    public static String trimToNull(String str) {
        str = str == null ? null : str.trim();
        return isEmpty(str) ? null : str;
    }

    /**
     * 字符串去左右空格.
     * <pre>
     * 例：
     * trimToNull(null)          = ""
     * trimToNull("")            = ""
     * trimToNull("     ")       = ""
     * trimToNull("abc")         = "abc"
     * trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str 要操作的字符串
     * @return 如果字符串为""或者" "或者null返回""，否则返回去除左右空格之后的字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 删除字符串中的所有空格.
     * <pre>
     * 例：
     * removeWhitespace(null) = null
     * removeWhitespace("") = ""
     * removeWhitespace(" ") = ""
     * removeWhitespace("abc") = "abc"
     * removeWhitespace("  ab c ") = "abc"
     * </pre>
     *
     * @param str 要操作的字符串
     * @return 删除所有空格之后的字符串
     */
    public static String removeWhitespace(String str) {
        int len = 0;
        if (str == null || (len = str.length()) == 0) {
            return str;
        }
        char[] chs = new char[len];
        int count = 0;
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (!Character.isWhitespace(ch)) {
                chs[count++] = ch;
            }
        }
        if (count == len) {
            return str;
        }
        return new String(chs, 0, count);
    }

    /**
     * 删除字符串的开头字符.
     * <pre>
     * 例：
     * removeStart(null,*) = null
     * removeStart("",*) = ""
     * removeStart(*,null) = *
     * removeStart("www.zfkj.com","www.") = "zfkj.com"
     * removeStart("domain.com","www.") = "domain.com"
     * removeStart("www.domain.com","domain") = "www.domain.com"
     * removeStart("abc","") = "abc"
     * </pre>
     *
     * @param str    要操作的字符串
     * @param remove 要删除的符合字符
     * @return 处理完成之后的字符串
     */
    public static String removeStart(String str, String remove) {
        if (str == null || str.length() == 0
            || remove == null || remove.length() == 0) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(0, remove.length());
        }
        return str;
    }

    /**
     * 删除字符串的开头字符.
     * <pre>
     * 例：
     * removeStart(null,*) = null
     * removeStart("",*) = ""
     * removeStart(*,null) = *
     * removeStart("www.zfkj.com","www.") = "zfkj.com"
     * removeStart("domain.com","www.") = "domain.com"
     * removeStart("www.domain.com","domain") = "www.domain.com"
     * removeStart("abc","") = "abc"
     * </pre>
     *
     * @param str        要操作的字符串
     * @param remove     要删除的符合字符
     * @param ignoreCase 忽略大小写
     * @return 处理完成之后的字符串
     */
    public static String removeStart(String str, String remove, boolean ignoreCase) {
        int rl;
        if (str == null || str.length() == 0
            || remove == null || (rl = remove.length()) == 0) {
            return str;
        }
        if (str.regionMatches(ignoreCase, 0, remove, 0, rl)) {
            return str.substring(0, rl);
        }
        return str;
    }

    /**
     * 删除字符串的结尾字符.
     * <pre>
     * 例：
     * removeEnd(null, *)      = null
     * removeEnd("", *)        = ""
     * removeEnd(*, null)      = *
     * removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * removeEnd("www.domain.com", ".com")   = "www.domain"
     * removeEnd("www.domain.com", "domain") = "www.domain.com"
     * removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str    要操作的字符串
     * @param remove 要删除的字符串
     * @return 处理完成的字符串
     */
    public static String removeEnd(String str, String remove) {
        int sl;
        int rl;
        if (str == null || (sl = str.length()) == 0
            || remove == null || (rl = remove.length()) == 0) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, sl - rl);
        }
        return str;
    }

    /**
     * 删除字符串的结尾字符.
     * <pre>
     * 例：
     * removeEnd(null, *)      = null
     * removeEnd("", *)        = ""
     * removeEnd(*, null)      = *
     * removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * removeEnd("www.domain.com", ".com")   = "www.domain"
     * removeEnd("www.domain.com", "domain") = "www.domain.com"
     * removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str        要操作的字符串
     * @param remove     要删除的字符串
     * @param ignoreCase 忽略大小写
     * @return 处理完成的字符串
     */
    public static String removeEnd(String str, String remove, boolean ignoreCase) {
        int sl;
        int rl;
        if (str == null || (sl = str.length()) == 0
            || remove == null || (rl = remove.length()) == 0) {
            return str;
        }
        if (str.regionMatches(ignoreCase, sl - rl, remove, 0, rl)) {
            return str.substring(0, remove.length());
        }
        return str;
    }

    /**
     * 删除字符串中出现的字符.
     * <pre>
     * 例：
     * remove(null, *)        = null
     * remove("", *)          = ""
     * remove(*, null)        = *
     * remove(*, "")          = *
     * remove("queued", "ue") = "qd"
     * remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str    要操作的字符
     * @param remove 要删除的字符
     * @return 删除之后的字符串
     */
    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, "", -1);
    }

    /**
     * 删除字符串中的字节.
     * <pre>
     * 例：
     * remove(null, *)       = null
     * remove("", *)         = ""
     * remove("queued", 'u') = "qeed"
     * remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str    要操作的字符串
     * @param remove 要删除的字节
     * @return 删除对应字节后的字符串
     */
    public static String remove(final String str, final char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    /**
     * 字符串替换.
     * <pre>
     * 例：
     * replace(null, *, *, *)         = null
     * replace("", *, *, *)           = ""
     * replace("any", null, *, *)     = "any"
     * replace("any", *, null, *)     = "any"
     * replace("any", "", *, *)       = "any"
     * replace("any", *, *, 0)        = "any"
     * replace("abaa", "a", null, -1) = "abaa"
     * replace("abaa", "a", "", -1)   = "b"
     * replace("abaa", "a", "z", 0)   = "abaa"
     * replace("abaa", "a", "z", 1)   = "zbaa"
     * replace("abaa", "a", "z", 2)   = "zbza"
     * replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param str        原始字符串
     * @param oldChar    即将替换的字符
     * @param newChar    新的字符
     * @param replaceMax 要替换多少个匹配 -1表示全部
     * @return 处理完成之后的字符串
     */
    public static String replace(String str, String oldChar, String newChar, int replaceMax) {
        int sl;
        int ol;
        if (str == null || (sl = str.length()) == 0
            || oldChar == null || (ol = oldChar.length()) == 0
            || newChar == null || replaceMax == 0) {
            return str;
        }
        //判断字符中是否包括了要替换的字符串
        int start = 0;
        int end;
        if ((end = str.indexOf(oldChar)) == -1) {
            return str;
        }
        int increase = newChar.length() - ol;
        increase = increase < 0 ? 0 : increase;
        if (replaceMax < 0) {
            replaceMax = 16;
        } else if (replaceMax > 64) {
            replaceMax = 64;
        }
        increase *= replaceMax;
        StringBuilder buf = new StringBuilder(sl + increase);
        while (end != -1) {
            buf.append(str.substring(start, end)).append(newChar);
            start = end + ol;
            if (--replaceMax == 0) {
                break;
            }
            end = str.indexOf(oldChar, start);
        }
        buf.append(str.substring(start));
        return buf.toString();
    }

    /**
     * 字符串替换.
     * <pre>
     * 例：
     * replace(null, *, *, *)         = null
     * replace("", *, *, *)           = ""
     * replace("any", null, *, *)     = "any"
     * replace("any", *, null, *)     = "any"
     * replace("any", "", *, *)       = "any"
     * replace("any", *, *, 0)        = "any"
     * replace("abaa", "a", null, -1) = "abaa"
     * replace("abaa", "a", "", -1)   = "b"
     * replace("abaa", "a", "z", 0)   = "abaa"
     * replace("abaa", "a", "z", 1)   = "zbaa"
     * replace("abaa", "a", "z", 2)   = "zbza"
     * replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text         原始字符串
     * @param searchString 即将替换的字符
     * @param replacement  新的字符
     * @return 处理完成之后的字符串
     */
    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }


    /**
     * 字符串覆盖.
     * <pre>
     * 例：
     * overlay(null, *, *, *)            = null
     * overlay("", "abc", 0, 0)          = "abc"
     * overlay("abcdef", null, 2, 4)     = "abef"
     * overlay("abcdef", "", 2, 4)       = "abef"
     * overlay("abcdef", "", 4, 2)       = "abef"
     * overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str     原始字符串
     * @param overlay 要覆盖的字符串
     * @param start   开始位置，如果小于0=0，如果大于原始字符串的长度，则为字符串的长度
     * @param end     结束位置，如果小于0=0，如果大于原始字符串的长度，则为字符串的长度 如果 end > start 则二者互换。
     * @return
     */
    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuilder(len + start - end + overlay.length() + 1)
            .append(str.substring(0, start))
            .append(overlay)
            .append(str.substring(end))
            .toString();
    }

    /**
     * 首字母大写.
     * <pre>
     * 例：
     * capitalize(null)  = null
     * capitalize("")    = ""
     * capitalize("cat") = "Cat"
     * capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str 要操作的字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen)
            .append(Character.toTitleCase(firstChar))
            .append(str.substring(1))
            .toString();
    }

    /**
     * 首字母小写.
     * <pre>
     * 例：
     * capitalize(null)  = null
     * capitalize("")    = ""
     * capitalize("Cat") = "cat"
     * capitalize("CAt") = "cAt"
     * </pre>
     *
     * @param str 要操作的字符串
     * @return 首字母小写的字符串
     */
    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            // already uncapitalized
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toLowerCase(firstChar))
            .append(str.substring(1))
            .toString();
    }

    /**
     * 大小写转换.
     * <pre>
     * 例：
     * swapCase(null)                 = null
     * swapCase("")                   = ""
     * swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * @param str 要操作的字符串
     * @return 大写变小写，小写变大写后的字符串
     */
    public static String swapCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char[] buffer = str.toCharArray();
        char ch;
        for (int i = 0, len = buffer.length; i < len; i++) {
            ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                buffer[i] = Character.toUpperCase(ch);
            }
        }
        return new String(buffer);
    }

    /**
     * 字符串数量匹配.
     * <pre>
     * 例：
     * countMatches(null, *)       = 0
     * countMatches("", *)         = 0
     * countMatches("abba", null)  = 0
     * countMatches("abba", "")    = 0
     * countMatches("abba", "a")   = 2
     * countMatches("abba", "ab")  = 1
     * countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str 要查找的字符串
     * @param sub 要匹配的字符串
     * @return 匹配的次数
     */
    public static int countMatches(String str, String sub) {
        int subLen;
        if (str == null || str.length() == 0
            || sub == null || (subLen = sub.length()) == 0) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += subLen;
        }
        return count;
    }

    /**
     * 字符串数量匹配.
     * <pre>
     * 例：
     * countMatches(null, *)  = 0
     * countMatches("", *)  = 0
     * countMatches("abba", 0) = 0
     * countMatches("abba", 'a') = 2
     * countMatches("abba", 'b') = 2
     * countMatches("abba", 'x') = 0
     * </pre>
     *
     * @param str 要查找的字符串
     * @param ch  要匹配的字符
     * @return 匹配的次数
     */
    public static int countMatches(String str, char ch) {
        int len;
        if (str == null || (len = str.length()) == 0) {
            return 0;
        }
        int count = 0;
        // We could also call str.toCharArray() for faster look ups
        // but that would generate more garbage.
        for (int i = 0; i < len; i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 字符串反转.
     * <pre>
     * 例：
     * reverse(null) = null
     * reverse("") = ""
     * reverse("bat") = "tab"
     * </pre>
     *
     * @param str 字符串
     * @return 反转之后的字符串
     */
    public static String reverse(String str) {
        if (isEmpty(str)) {
            return str;
        }
        final char[] value = str.toCharArray();
        int count = value.length;
        boolean hasSurrogates = false;
        int n = count - 1;
        for (int j = (n - 1) >> 1; j >= 0; j--) {
            int k = n - j;
            char cj = value[j];
            char ck = value[k];
            value[j] = ck;
            value[k] = cj;
            if (Character.isSurrogate(cj) || Character.isSurrogate(ck)) {
                hasSurrogates = true;
            }
        }
        if (hasSurrogates) {
            for (int i = 0; i < count - 1; i++) {
                char c2 = value[i];
                if (Character.isLowSurrogate(c2)) {
                    char c1 = value[i + 1];
                    if (Character.isHighSurrogate(c1)) {
                        value[i++] = c1;
                        value[i] = c2;
                    }
                }
            }
        }
        return new String(value);
    }

    /**
     * 字符串省略.
     * <pre>
     * 例：
     * abbreviate(null, *)      = null
     * abbreviate("", 4)        = ""
     * abbreviate("abcdefg", 6) = "abc..."
     * abbreviate("abcdefg", 7) = "abcdefg"
     * abbreviate("abcdefg", 8) = "abcdefg"
     * abbreviate("abcdefg", 4) = "a..."
     * abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str      要操作的字符串
     * @param maxWidth 最长长度 要求>=4
     * @return 省略之后的字符串
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * 字符串省略.
     * <pre>
     * 例：
     * abbreviate(null, *) = null
     * abbreviate("", 4) = ""
     * abbreviate("abcdefg", 6) = "abc..."
     * abbreviate("abcdefg", 7) = "abcdefg"
     * abbreviate("abcdefg", 8) = "abcdefg"
     * abbreviate("abcdefg", 4) = "a..."
     * abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str      要操作的字符串
     * @param offset   从哪个位置开始
     * @param maxWidth 最长长度 要求>=4
     * @return 省略之后的字符串
     */
    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        int strLen = str.length();
        if (strLen <= maxWidth) {
            return str;
        }
        if (offset > strLen) {
            offset = strLen;
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = strLen - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + maxWidth - 3 < strLen) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(strLen - (maxWidth - 3));
    }

    /**
     * 字符串中间缩略.
     * <pre>
     * 例：
     * abbreviateMiddle(null, null, 0)      = null
     * abbreviateMiddle("abc", null, 0)      = "abc"
     * abbreviateMiddle("abc", ".", 0)      = "abc"
     * abbreviateMiddle("abc", ".", 3)      = "abc"
     * abbreviateMiddle("abcdef", ".", 4)     = "ab.f"
     * </pre>
     *
     * @param str    要缩略操作的字符串
     * @param middle 要替换的字符串
     * @param length 要保存字符串的长度 如果要保存的长度>=原始字符串长度 或者  原始字符串长度 < 要替换的字符串长度+2 直接返回原始字符串
     * @return
     */
    public static String abbreviateMiddle(String str, String middle, int length) {
        int sl;
        int ml;
        if (str == null || (sl = str.length()) == 0
            || middle == null || (ml = middle.length()) == 0) {
            return str;
        }
        if (length >= sl || length < ml + 2) {
            return str;
        }
        int targetSting = length - ml;
        int startOffset = targetSting / 2 + targetSting % 2;
        int endOffset = sl - targetSting / 2;

        StringBuilder builder = new StringBuilder(length);
        builder.append(str.substring(0, startOffset));
        builder.append(middle);
        builder.append(str.substring(endOffset));
        return builder.toString();
    }

    /**
     * 缩写.
     *
     * @param str   字符串
     * @param split 分割符
     * @return 缩写之后的字符串
     */
    public static String toAbbreviation(String str, char split) {
        if (str == null || str.length() == 0) {
            return "";
        }
        char[] chars = str.toCharArray();
        char c;
        StringBuilder builder = new StringBuilder();
        builder.append(chars[0]);
        for (int i = 1, l = chars.length; i < l; i++) {
            c = chars[i];
            if (c == split) {
                builder.append(chars[++i]);
            }
        }
        return builder.toString();
    }

    /**
     * 转驼峰命名格式.
     *
     * @param str     字符串
     * @param firstUp 是否首字母大写
     * @return 驼峰命名的字符串
     */
    public static String toCamel(String str, boolean firstUp) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char[] buffer = str.toCharArray();
        StringBuilder builder = new StringBuilder(buffer.length);
        for (int i = 0, l = buffer.length; i < l; i++) {
            char c = buffer[i];
            if (c == '_') {
                builder.append(Character.toUpperCase(buffer[++i]));
            } else {
                builder.append((i == 0 && firstUp) ? Character.toUpperCase(c) : c);
            }
        }
        return builder.toString();
    }

    /**
     * 转final命名格式.
     *
     * @param str 字符串
     * @return final命名格式
     */
    public static String toFinal(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char[] buffer = str.toCharArray();
        char ch;
        //标识上一位是否为大写
        byte prevCharCase = 0x0000;
        //表示大写：0x0001
        //表示小写：0x0010
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = buffer.length; i < len; i++) {
            ch = buffer[i];
            //当前字节为小写
            if (Character.isLowerCase(ch)) {
                prevCharCase = 0x0001;
                builder.append(Character.toUpperCase(ch));
            } else {
                //小写转大写
                if (prevCharCase == 0x0001) {
                    builder.append('_');
                }
                prevCharCase = 0x0002;
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    /**
     * 格式化字符串.
     *
     * <p>用来格式化字符串中携带的{}字符，使用参数进行替换</p>
     *
     * @param str  字符串
     * @param args 参数
     * @return 格式化之后的字符串
     */
    public static String format(String str, Object... args) {
        char[] chars = str.toCharArray();
        int l = str.length();
        int i = 0;
        StringBuilder builder = new StringBuilder(l + (l >> 1));
        byte skip = 0;
        for (char c : chars) {
            switch (c) {
                case '{':
                    skip = 1;
                    break;
                case '}':
                    if (skip == 1) {
                        //{}
                        builder.append(args[i++]);
                    }
                    skip = 0;
                    break;
                default:
                    if (skip == 0) {
                        builder.append(c);
                    } else if (skip == 1) {
                        builder.append(args[i++]);
                        skip = 2;
                    }
            }
        }
        return builder.toString();
    }

    /**
     * 判断长度.
     *
     * @param min  最小长度
     * @param max  最大长度
     * @param text 文本
     * @return 是否在范围之间
     */
    public static boolean isLengthIn(int min, int max, String text) {
        if (text == null) {
            return min <= 0 && max >= 0;
        }
        int len = text.length();
        return min <= len && max >= len;
    }


    /**
     * 字符串追加.
     *
     * @param d 默认字符串
     * @param f 连接字符串
     * @param a 字符串A
     * @param b 字符串B
     * @return 处理之后的字符串
     */
    public static String getAppend(String d, String f, String a, String b) {
        if (a == null && b == null) {
            return d;
        }
        if (b == null) {
            return a;
        }
        if (a == null) {
            return b;
        }
        return a + f + b;
    }
}
