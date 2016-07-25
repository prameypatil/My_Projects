package application;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DuplicateRemoverTest {

    DuplicateRemover duplicateRemover;

    @Before
    public void setUp()
    {
        duplicateRemover = new DuplicateRemover();
    }

    @Test
    public void RemoveDuplicateWordsRemovesDuplicateWords()
    {

        assertEquals( "really", duplicateRemover.RemoveDuplicateWords("really really") );
    }

    @Test
    public void RemoveDuplicateWordsRemovesMultipleDuplicateWords()
    {

        assertEquals( "is really", duplicateRemover.RemoveDuplicateWords("is is really really") );
    }

    @Test
    public void RemoveDuplicateWordsRemovesOnlyConsecutiveDuplicateWords()
    {

        assertEquals( "this is really is", duplicateRemover.RemoveDuplicateWords("this is really really is") );
    }

    @Test
    public void RemoveDuplicateWordsRemovesDuplicateWordsAtTheEndOfSentence()
    {

        assertEquals( "this is really stupid", duplicateRemover.RemoveDuplicateWords("this is really stupid stupid") );
    }


    @Test
    public void RemoveDuplicateWordsRemovesDuplicateWordsAtTheStartOfSentence()
    {

        assertEquals( "this is really stupid", duplicateRemover.RemoveDuplicateWords("this this is really stupid") );
    }


    @Test
    public void RemoveDuplicateWordsRemovesConsecutiveDuplicateWordsOccurredMoreThanTwice()
    {

        assertEquals( "this is really stupid", duplicateRemover.RemoveDuplicateWords("this is really really really stupid") );
    }


    @Test
    public void RemoveDuplicateWordsDoesNotRemoveWordsWithSlightSpellingDifference()
    {

        assertEquals( "this is really raelly stupid", duplicateRemover.RemoveDuplicateWords("this is really raelly stupid") );
    }
}
