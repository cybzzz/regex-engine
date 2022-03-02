import org.junit.Test;
import regex.Atom;
import regex.Parser;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ParseTest {
    @Test
    public void basicTest() {
        String s1 = "(a|b)*c";
        String s2 = "abc";
        String s3 = "a|b|c";
        ArrayList<Atom> list = Parser.insertExplicitConcatOperator(s1);
        ArrayList<Atom> postfix = Parser.toPostfix(list);
        assertEquals(list.size(), 8);
        assertEquals(postfix.toString(), "[a, b, |, *, c, &]");
        list = Parser.insertExplicitConcatOperator(s2);
        postfix = Parser.toPostfix(list);
        assertEquals(list.size(), 5);
        assertEquals(postfix.toString(), "[a, b, &, c, &]");
        list = Parser.insertExplicitConcatOperator(s3);
        postfix = Parser.toPostfix(list);
        assertEquals(list.size(), 5);
        assertEquals(postfix.toString(), "[a, b, |, c, |]");
    }

    @Test
    public void complexTest() {
        String s1 = "\\d{1,5}|.c";
        String s2 = "ac\\D?d";
        String s3 = ".+(cc|a)";
        ArrayList<Atom> postfix = Parser.toPostfix(Parser.insertExplicitConcatOperator(s1));
        assertEquals(postfix.toString(), "[\\d, {1,5}, ., c, &, |]");
        postfix = Parser.toPostfix(Parser.insertExplicitConcatOperator(s2));
        assertEquals(postfix.toString(), "[a, c, &, \\D, ?, &, d, &]");
        postfix = Parser.toPostfix(Parser.insertExplicitConcatOperator(s3));
        assertEquals(postfix.toString(), "[., +, c, c, &, a, |, &]");
    }
}
