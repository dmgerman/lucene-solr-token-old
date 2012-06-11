begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.wikipedia
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|wikipedia
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|HashSet
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
name|BaseTokenStreamTestCase
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
name|Tokenizer
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
name|tokenattributes
operator|.
name|FlagsAttribute
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|wikipedia
operator|.
name|WikipediaTokenizer
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Basic Tests for {@link WikipediaTokenizer}  **/
end_comment
begin_class
DECL|class|WikipediaTokenizerTest
specifier|public
class|class
name|WikipediaTokenizerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|LINK_PHRASES
specifier|protected
specifier|static
specifier|final
name|String
name|LINK_PHRASES
init|=
literal|"click [[link here again]] click [http://lucene.apache.org here again] [[Category:a b c d]]"
decl_stmt|;
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"This is a [[Category:foo]]"
decl_stmt|;
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"foo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|8
block|,
literal|21
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|,
literal|9
block|,
literal|24
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|CATEGORY
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|, }
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testHandwritten
specifier|public
name|void
name|testHandwritten
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure all tokens are in only one type
name|String
name|test
init|=
literal|"[[link]] This is a [[Category:foo]] Category  This is a linked [[:Category:bar none withstanding]] "
operator|+
literal|"Category This is (parens) This is a [[link]]  This is an external URL [http://lucene.apache.org] "
operator|+
literal|"Here is ''italics'' and ''more italics'', '''bold''' and '''''five quotes''''' "
operator|+
literal|" This is a [[link|display info]]  This is a period.  Here is $3.25 and here is 3.50.  Here's Johnny.  "
operator|+
literal|"==heading== ===sub head=== followed by some text  [[Category:blah| ]] "
operator|+
literal|"''[[Category:ital_cat]]''  here is some that is ''italics [[Category:foo]] but is never closed."
operator|+
literal|"'''same [[Category:foo]] goes for this '''''and2 [[Category:foo]] and this"
operator|+
literal|" [http://foo.boo.com/test/test/ Test Test] [http://foo.boo.com/test/test/test.html Test Test]"
operator|+
literal|" [http://foo.boo.com/test/test/test.html?g=b&c=d Test Test]<ref>Citation</ref><sup>martian</sup><span class=\"glue\">code</span>"
decl_stmt|;
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"link"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"foo"
block|,
literal|"Category"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"linked"
block|,
literal|"bar"
block|,
literal|"none"
block|,
literal|"withstanding"
block|,
literal|"Category"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"parens"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"link"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"an"
block|,
literal|"external"
block|,
literal|"URL"
block|,
literal|"http://lucene.apache.org"
block|,
literal|"Here"
block|,
literal|"is"
block|,
literal|"italics"
block|,
literal|"and"
block|,
literal|"more"
block|,
literal|"italics"
block|,
literal|"bold"
block|,
literal|"and"
block|,
literal|"five"
block|,
literal|"quotes"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"link"
block|,
literal|"display"
block|,
literal|"info"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"period"
block|,
literal|"Here"
block|,
literal|"is"
block|,
literal|"3.25"
block|,
literal|"and"
block|,
literal|"here"
block|,
literal|"is"
block|,
literal|"3.50"
block|,
literal|"Here's"
block|,
literal|"Johnny"
block|,
literal|"heading"
block|,
literal|"sub"
block|,
literal|"head"
block|,
literal|"followed"
block|,
literal|"by"
block|,
literal|"some"
block|,
literal|"text"
block|,
literal|"blah"
block|,
literal|"ital"
block|,
literal|"cat"
block|,
literal|"here"
block|,
literal|"is"
block|,
literal|"some"
block|,
literal|"that"
block|,
literal|"is"
block|,
literal|"italics"
block|,
literal|"foo"
block|,
literal|"but"
block|,
literal|"is"
block|,
literal|"never"
block|,
literal|"closed"
block|,
literal|"same"
block|,
literal|"foo"
block|,
literal|"goes"
block|,
literal|"for"
block|,
literal|"this"
block|,
literal|"and2"
block|,
literal|"foo"
block|,
literal|"and"
block|,
literal|"this"
block|,
literal|"http://foo.boo.com/test/test/"
block|,
literal|"Test"
block|,
literal|"Test"
block|,
literal|"http://foo.boo.com/test/test/test.html"
block|,
literal|"Test"
block|,
literal|"Test"
block|,
literal|"http://foo.boo.com/test/test/test.html?g=b&c=d"
block|,
literal|"Test"
block|,
literal|"Test"
block|,
literal|"Citation"
block|,
literal|"martian"
block|,
literal|"code"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|INTERNAL_LINK
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|CATEGORY
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|CATEGORY
block|,
name|CATEGORY
block|,
name|CATEGORY
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|INTERNAL_LINK
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|EXTERNAL_LINK_URL
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|ITALICS
block|,
literal|"<ALPHANUM>"
block|,
name|ITALICS
block|,
name|ITALICS
block|,
name|BOLD
block|,
literal|"<ALPHANUM>"
block|,
name|BOLD_ITALICS
block|,
name|BOLD_ITALICS
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|INTERNAL_LINK
block|,
name|INTERNAL_LINK
block|,
name|INTERNAL_LINK
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<NUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<NUM>"
block|,
literal|"<APOSTROPHE>"
block|,
literal|"<ALPHANUM>"
block|,
name|HEADING
block|,
name|SUB_HEADING
block|,
name|SUB_HEADING
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|CATEGORY
block|,
name|CATEGORY
block|,
name|CATEGORY
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|ITALICS
block|,
name|CATEGORY
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|BOLD
block|,
name|CATEGORY
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|BOLD_ITALICS
block|,
name|CATEGORY
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
name|EXTERNAL_LINK_URL
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK_URL
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK_URL
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK
block|,
name|CITATION
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinkPhrases
specifier|public
name|void
name|testLinkPhrases
parameter_list|()
throws|throws
name|Exception
block|{
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|LINK_PHRASES
argument_list|)
argument_list|)
decl_stmt|;
name|checkLinkPhrases
argument_list|(
name|tf
argument_list|)
expr_stmt|;
block|}
DECL|method|checkLinkPhrases
specifier|private
name|void
name|checkLinkPhrases
parameter_list|(
name|WikipediaTokenizer
name|tf
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"click"
block|,
literal|"link"
block|,
literal|"here"
block|,
literal|"again"
block|,
literal|"click"
block|,
literal|"http://lucene.apache.org"
block|,
literal|"here"
block|,
literal|"again"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinks
specifier|public
name|void
name|testLinks
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"[http://lucene.apache.org/java/docs/index.html#news here] [http://lucene.apache.org/java/docs/index.html?b=c here] [https://lucene.apache.org/java/docs/index.html?b=c here]"
decl_stmt|;
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"http://lucene.apache.org/java/docs/index.html#news"
block|,
literal|"here"
block|,
literal|"http://lucene.apache.org/java/docs/index.html?b=c"
block|,
literal|"here"
block|,
literal|"https://lucene.apache.org/java/docs/index.html?b=c"
block|,
literal|"here"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|EXTERNAL_LINK_URL
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK_URL
block|,
name|EXTERNAL_LINK
block|,
name|EXTERNAL_LINK_URL
block|,
name|EXTERNAL_LINK
block|, }
argument_list|)
expr_stmt|;
block|}
DECL|method|testLucene1133
specifier|public
name|void
name|testLucene1133
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|untoks
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|untoks
operator|.
name|add
argument_list|(
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|untoks
operator|.
name|add
argument_list|(
name|WikipediaTokenizer
operator|.
name|ITALICS
argument_list|)
expr_stmt|;
comment|//should be exactly the same, regardless of untoks
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|LINK_PHRASES
argument_list|)
argument_list|,
name|WikipediaTokenizer
operator|.
name|TOKENS_ONLY
argument_list|,
name|untoks
argument_list|)
decl_stmt|;
name|checkLinkPhrases
argument_list|(
name|tf
argument_list|)
expr_stmt|;
name|String
name|test
init|=
literal|"[[Category:a b c d]] [[Category:e f g]] [[link here]] [[link there]] ''italics here'' something ''more italics'' [[Category:h   i   j]]"
decl_stmt|;
name|tf
operator|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|,
name|WikipediaTokenizer
operator|.
name|UNTOKENIZED_ONLY
argument_list|,
name|untoks
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a b c d"
block|,
literal|"e f g"
block|,
literal|"link"
block|,
literal|"here"
block|,
literal|"link"
block|,
literal|"there"
block|,
literal|"italics here"
block|,
literal|"something"
block|,
literal|"more italics"
block|,
literal|"h   i   j"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|32
block|,
literal|42
block|,
literal|47
block|,
literal|56
block|,
literal|61
block|,
literal|71
block|,
literal|86
block|,
literal|98
block|,
literal|124
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|18
block|,
literal|37
block|,
literal|46
block|,
literal|51
block|,
literal|60
block|,
literal|66
block|,
literal|83
block|,
literal|95
block|,
literal|110
block|,
literal|133
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoth
specifier|public
name|void
name|testBoth
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|untoks
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|untoks
operator|.
name|add
argument_list|(
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|untoks
operator|.
name|add
argument_list|(
name|WikipediaTokenizer
operator|.
name|ITALICS
argument_list|)
expr_stmt|;
name|String
name|test
init|=
literal|"[[Category:a b c d]] [[Category:e f g]] [[link here]] [[link there]] ''italics here'' something ''more italics'' [[Category:h   i   j]]"
decl_stmt|;
comment|//should output all the indivual tokens plus the untokenized tokens as well.  Untokenized tokens
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOTH
argument_list|,
name|untoks
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a b c d"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e f g"
block|,
literal|"e"
block|,
literal|"f"
block|,
literal|"g"
block|,
literal|"link"
block|,
literal|"here"
block|,
literal|"link"
block|,
literal|"there"
block|,
literal|"italics here"
block|,
literal|"italics"
block|,
literal|"here"
block|,
literal|"something"
block|,
literal|"more italics"
block|,
literal|"more"
block|,
literal|"italics"
block|,
literal|"h   i   j"
block|,
literal|"h"
block|,
literal|"i"
block|,
literal|"j"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|11
block|,
literal|13
block|,
literal|15
block|,
literal|17
block|,
literal|32
block|,
literal|32
block|,
literal|34
block|,
literal|36
block|,
literal|42
block|,
literal|47
block|,
literal|56
block|,
literal|61
block|,
literal|71
block|,
literal|71
block|,
literal|79
block|,
literal|86
block|,
literal|98
block|,
literal|98
block|,
literal|103
block|,
literal|124
block|,
literal|124
block|,
literal|128
block|,
literal|132
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|18
block|,
literal|12
block|,
literal|14
block|,
literal|16
block|,
literal|18
block|,
literal|37
block|,
literal|33
block|,
literal|35
block|,
literal|37
block|,
literal|46
block|,
literal|51
block|,
literal|60
block|,
literal|66
block|,
literal|83
block|,
literal|78
block|,
literal|83
block|,
literal|95
block|,
literal|110
block|,
literal|102
block|,
literal|110
block|,
literal|133
block|,
literal|125
block|,
literal|129
block|,
literal|133
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|// now check the flags, TODO: add way to check flags from BaseTokenStreamTestCase?
name|tf
operator|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOTH
argument_list|,
name|untoks
argument_list|)
expr_stmt|;
name|int
name|expectedFlags
index|[]
init|=
operator|new
name|int
index|[]
block|{
name|UNTOKENIZED_TOKEN_FLAG
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
name|UNTOKENIZED_TOKEN_FLAG
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
name|UNTOKENIZED_TOKEN_FLAG
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
name|UNTOKENIZED_TOKEN_FLAG
block|,
literal|0
block|,
literal|0
block|,
name|UNTOKENIZED_TOKEN_FLAG
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|FlagsAttribute
name|flagsAtt
init|=
name|tf
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tf
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedFlags
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"flags "
operator|+
name|i
argument_list|,
name|expectedFlags
index|[
name|i
index|]
argument_list|,
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|tf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random large strings through the analyzer */
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
