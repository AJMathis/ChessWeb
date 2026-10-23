public class SQRUtils {
    public static int toIndex(String sqr) {
        char[] parts = sqr.toCharArray();
        return ((parts[1]-'1') * 8) + (parts[0]-'a');
    }
    public static String toString(int index) {
        int rank = index / 8;
        int file = index % 8;
        return (String.valueOf((char)(rank + '1') + ((char)(file + 'a'))));
    }

}
