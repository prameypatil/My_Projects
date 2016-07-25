package application;

public class DuplicateRemover {

    private String string, previousWord, currentWord, finalString = "";

    public String RemoveDuplicateWords( String theString ) {

        applyDuplicateRemover( theString );

        return ( finalString + " " + currentWord ).trim();
    }

    public void applyDuplicateRemover( String theString )
    {

        string = theString;

        for ( int i = 0; i < string.length(); i++ ) {

            if( string.charAt(i) == ' ' )
            {
                getCurrentWord(i);
            }
            else
            {
                if ( i == 0 ){

                    previousWord = Character.toString( string.charAt(i) );
                }
                else
                {
                    previousWord = previousWord + string.charAt(i);
                }
            }
        }
    }

    public void getCurrentWord( int start )
    {
        currentWord = "";

        for ( int i = start + 1; i < string.length() ; i++ ) {

            if ( string.charAt(i) == ' ' )
            {
                removeDuplication();
                break;
            }
            else {
                currentWord = currentWord + string.charAt(i);
            }

            if ( i == string.length() - 1 )
            {
                removeDuplication();
            }
        }
    }

    public void removeDuplication()
    {

        if ( !currentWord.equals(previousWord) )
        {
            if ( finalString.equals("") )
            {
                finalString = previousWord;
            }
            else { finalString = finalString + " " + previousWord; }
        }

        previousWord = "";
    }
}