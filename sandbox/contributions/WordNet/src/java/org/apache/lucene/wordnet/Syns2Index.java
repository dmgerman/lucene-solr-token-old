begin_unit
begin_package
DECL|package|org.apache.lucene.wordnet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|wordnet
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import
begin_comment
comment|/**  * Convert the prolog file wn_s.pl from the wordnet prolog download  * into a Lucene index suitable for looking up synonyms.  * The index is named 'syn_index' and has fields named "word"  * and "syn".  *<p>  * The source word (such as 'big') can be looked up in the  * "word" field, and if present there will be fields named "syn"  * for every synonym.  *</p>  *<p>  * While the wordnet file distinguishes groups of synonyms with  * related meanings we don't do that here.  *</p>  *<p>  * By default, with no args, we expect the prolog  * file to be at 'c:/proj/wordnet/prolog/wn_s.pl' and will  * write to an index named 'syn_index' in the current dir.  * See constants at the bottom of this file to change these.  *</p>  * See also:  *<br/>  * http://www.cogsci.princeton.edu/~wn/  *<br/>  * http://www.tropo.com/techno/java/lucene/wordnet.html  *  * @author Dave Spencer, dave@lumos.com  */
end_comment
begin_class
DECL|class|Syns2Index
specifier|public
class|class
name|Syns2Index
block|{
DECL|field|ana
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ana
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
comment|/**      * Takes optional arg of prolog file name.      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// get command line arguments
name|String
name|prologFilename
init|=
literal|null
decl_stmt|;
name|String
name|indexDir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|prologFilename
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|indexDir
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// ensure that the prolog file is readable
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|prologFilename
argument_list|)
operator|)
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: cannot read Prolog file: "
operator|+
name|prologFilename
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// exit if the target index directory already exists
if|if
condition|(
operator|(
operator|new
name|File
argument_list|(
name|indexDir
argument_list|)
operator|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: index directory already exists: "
operator|+
name|indexDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Please specify a name of a non-existent directory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Opening Prolog file "
operator|+
name|prologFilename
argument_list|)
expr_stmt|;
specifier|final
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|prologFilename
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
comment|// maps a word to all the "groups" it's in
specifier|final
name|Map
name|word2Nums
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|// maps a group to all the words in it
specifier|final
name|Map
name|num2Words
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|// number of rejected words
name|int
name|ndecent
init|=
literal|0
decl_stmt|;
comment|// status output
name|int
name|mod
init|=
literal|1
decl_stmt|;
name|int
name|row
init|=
literal|1
decl_stmt|;
comment|// parse prolog file
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// occasional progress
if|if
condition|(
operator|(
operator|++
name|row
operator|)
operator|%
name|mod
operator|==
literal|0
condition|)
block|{
name|mod
operator|*=
literal|2
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
operator|+
name|row
operator|+
literal|" "
operator|+
name|line
operator|+
literal|" "
operator|+
name|word2Nums
operator|.
name|size
argument_list|()
operator|+
literal|" "
operator|+
name|num2Words
operator|.
name|size
argument_list|()
operator|+
literal|" ndecent="
operator|+
name|ndecent
argument_list|)
expr_stmt|;
block|}
comment|// syntax check
if|if
condition|(
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"s("
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"OUCH: "
operator|+
name|line
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// parse line
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|int
name|comma
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|String
name|num
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|comma
argument_list|)
decl_stmt|;
name|int
name|q1
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'\''
argument_list|)
decl_stmt|;
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
name|q1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|q2
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'\''
argument_list|)
decl_stmt|;
name|String
name|word
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|q2
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
comment|// make sure is a normal word
if|if
condition|(
operator|!
name|isDecent
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|ndecent
operator|++
expr_stmt|;
continue|continue;
comment|// don't store words w/ spaces
block|}
comment|// 1/2: word2Nums map
comment|// append to entry or add new one
name|List
name|lis
init|=
operator|(
name|List
operator|)
name|word2Nums
operator|.
name|get
argument_list|(
name|word
argument_list|)
decl_stmt|;
if|if
condition|(
name|lis
operator|==
literal|null
condition|)
block|{
name|lis
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|lis
operator|.
name|add
argument_list|(
name|num
argument_list|)
expr_stmt|;
name|word2Nums
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|lis
argument_list|)
expr_stmt|;
block|}
else|else
name|lis
operator|.
name|add
argument_list|(
name|num
argument_list|)
expr_stmt|;
comment|// 2/2: num2Words map
name|lis
operator|=
operator|(
name|List
operator|)
name|num2Words
operator|.
name|get
argument_list|(
name|num
argument_list|)
expr_stmt|;
if|if
condition|(
name|lis
operator|==
literal|null
condition|)
block|{
name|lis
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|lis
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
name|num2Words
operator|.
name|put
argument_list|(
name|num
argument_list|,
name|lis
argument_list|)
expr_stmt|;
block|}
else|else
name|lis
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
comment|// close the streams
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// create the index
name|index
argument_list|(
name|indexDir
argument_list|,
name|word2Nums
argument_list|,
name|num2Words
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks to see if a word contains only alphabetic characters by      * checking it one character at a time.      *      * @param s string to check      * @return<code>true</code> if the string is decent      */
DECL|method|isDecent
specifier|private
specifier|static
name|boolean
name|isDecent
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetter
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Forms a Lucene index based on the 2 maps.      *      * @param indexDir the direcotry where the index should be created      * @param word2Nums      * @param num2Words      */
DECL|method|index
specifier|private
specifier|static
name|void
name|index
parameter_list|(
name|String
name|indexDir
parameter_list|,
name|Map
name|word2Nums
parameter_list|,
name|Map
name|num2Words
parameter_list|)
throws|throws
name|Throwable
block|{
name|int
name|row
init|=
literal|0
decl_stmt|;
name|int
name|mod
init|=
literal|1
decl_stmt|;
comment|// override the specific index if it already exists
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|ana
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Iterator
name|i1
init|=
name|word2Nums
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i1
operator|.
name|hasNext
argument_list|()
condition|)
comment|// for each word
block|{
name|String
name|g
init|=
operator|(
name|String
operator|)
name|i1
operator|.
name|next
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|index
argument_list|(
name|word2Nums
argument_list|,
name|num2Words
argument_list|,
name|g
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"word"
argument_list|,
name|g
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|++
name|row
operator|%
name|mod
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"row="
operator|+
name|row
operator|+
literal|" doc= "
operator|+
name|doc
argument_list|)
expr_stmt|;
name|mod
operator|*=
literal|2
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// else degenerate
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Given the 2 maps fills a document for 1 word.      */
DECL|method|index
specifier|private
specifier|static
name|int
name|index
parameter_list|(
name|Map
name|word2Nums
parameter_list|,
name|Map
name|num2Words
parameter_list|,
name|String
name|g
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|Throwable
block|{
name|List
name|keys
init|=
operator|(
name|List
operator|)
name|word2Nums
operator|.
name|get
argument_list|(
name|g
argument_list|)
decl_stmt|;
comment|// get list of key#'s
name|Iterator
name|i2
init|=
name|keys
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Set
name|already
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
comment|// keep them sorted
comment|// pass 1: fill up 'already' with all words
while|while
condition|(
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
comment|// for each key#
block|{
name|already
operator|.
name|addAll
argument_list|(
operator|(
name|List
operator|)
name|num2Words
operator|.
name|get
argument_list|(
name|i2
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// get list of words
block|}
name|int
name|num
init|=
literal|0
decl_stmt|;
name|already
operator|.
name|remove
argument_list|(
name|g
argument_list|)
expr_stmt|;
comment|// of course a word is it's own syn
name|Iterator
name|it
init|=
name|already
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|cur
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// don't store things like 'pit bull' -> 'american pit bull'
if|if
condition|(
operator|!
name|isDecent
argument_list|(
name|cur
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|num
operator|++
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"syn"
argument_list|,
name|cur
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|num
return|;
block|}
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n"
operator|+
literal|"java org.apache.lucene.wordnet.Syn2Index<prolog file><index dir>\n\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
