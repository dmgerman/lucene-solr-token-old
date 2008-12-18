begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
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
name|TokenStream
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
name|Token
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
name|WhitespaceTokenizer
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
name|io
operator|.
name|StringReader
import|;
end_import
begin_comment
comment|/**  * New WordDelimiterFilter tests... most of the tests are in ConvertedLegacyTest  */
end_comment
begin_class
DECL|class|TestWordDelimiterFilter
specifier|public
class|class
name|TestWordDelimiterFilter
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"solr/conf/schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solr/conf/solrconfig.xml"
return|;
block|}
DECL|method|posTst
specifier|public
name|void
name|posTst
parameter_list|(
name|String
name|v1
parameter_list|,
name|String
name|v2
parameter_list|,
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"subword"
argument_list|,
name|v1
argument_list|,
literal|"subword"
argument_list|,
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// there is a positionIncrementGap of 100 between field values, so
comment|// we test if that was maintained.
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~90"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~110"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRetainPositionIncrement
specifier|public
name|void
name|testRetainPositionIncrement
parameter_list|()
block|{
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"123"
argument_list|,
literal|"456"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/"
argument_list|,
literal|"/456/"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/abc"
argument_list|,
literal|"qwe/456/"
argument_list|,
literal|"abc"
argument_list|,
literal|"qwe"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo"
argument_list|,
literal|"bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo-123"
argument_list|,
literal|"456-bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoGenerationEdgeCase
specifier|public
name|void
name|testNoGenerationEdgeCase
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"222"
argument_list|,
literal|"numberpartfail"
argument_list|,
literal|"123.123.123.123"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreCaseChange
specifier|public
name|void
name|testIgnoreCaseChange
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"wdf_nocase"
argument_list|,
literal|"HellO WilliAM"
argument_list|,
literal|"subword"
argument_list|,
literal|"GoodBye JonEs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no case change"
argument_list|,
name|req
argument_list|(
literal|"wdf_nocase:(hell o am)"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"case change"
argument_list|,
name|req
argument_list|(
literal|"subword:(good jon)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPreserveOrignalTrue
specifier|public
name|void
name|testPreserveOrignalTrue
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"144"
argument_list|,
literal|"wdf_preserve"
argument_list|,
literal|"404-123"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:404"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:123"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:404-123*"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
comment|/***   public void testPerformance() throws IOException {     String s = "now is the time-for all good men to come to-the aid of their country.";     Token tok = new Token();     long start = System.currentTimeMillis();     int ret=0;     for (int i=0; i<1000000; i++) {       StringReader r = new StringReader(s);       TokenStream ts = new WhitespaceTokenizer(r);       ts = new WordDelimiterFilter(ts, 1,1,1,1,0);        while (ts.next(tok) != null) ret++;     }      System.out.println("ret="+ret+" time="+(System.currentTimeMillis()-start));   }   ***/
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test that subwords and catenated subwords have
comment|// the correct offsets.
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
name|Token
name|t
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"foo-bar"
argument_list|,
literal|5
argument_list|,
literal|12
argument_list|)
expr_stmt|;
comment|// actual
return|return
name|t
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Token
name|t
init|;
operator|(
name|t
operator|=
name|wdf
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|String
name|termText
init|=
operator|new
name|String
argument_list|(
name|t
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|termText
operator|.
name|equals
argument_list|(
literal|"foo"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|termText
operator|.
name|equals
argument_list|(
literal|"bar"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|termText
operator|.
name|equals
argument_list|(
literal|"foobar"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// make sure all 3 tokens were generated
comment|// test that if splitting or catenating a synonym, that the offsets
comment|// are not altered (they would be incorrect).
name|wdf
operator|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
name|Token
name|t
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"foo-bar"
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|// a synonym
return|return
name|t
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
name|t
init|;
operator|(
name|t
operator|=
name|wdf
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOffsetChange
specifier|public
name|void
name|testOffsetChange
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
name|Token
name|t
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"Ã¼belkeit)"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|t
init|=
name|wdf
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ã¼belkeit"
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsetChange2
specifier|public
name|void
name|testOffsetChange2
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
name|Token
name|t
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"(Ã¼belkeit"
argument_list|,
literal|7
argument_list|,
literal|17
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|t
init|=
name|wdf
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ã¼belkeit"
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|17
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsetChange3
specifier|public
name|void
name|testOffsetChange3
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
name|Token
name|t
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"(Ã¼belkeit"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|t
init|=
name|wdf
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ã¼belkeit"
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsetChange4
specifier|public
name|void
name|testOffsetChange4
parameter_list|()
throws|throws
name|Exception
block|{
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
specifier|private
name|Token
name|t
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
return|return
literal|null
return|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"(foo,bar)"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|t
init|=
name|wdf
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|wdf
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|t
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlphaNumericWords
specifier|public
name|void
name|testAlphaNumericWords
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"68"
argument_list|,
literal|"numericsubword"
argument_list|,
literal|"Java/J2SE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"j2se found"
argument_list|,
name|req
argument_list|(
literal|"numericsubword:(J2SE)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no j2 or se"
argument_list|,
name|req
argument_list|(
literal|"numericsubword:(J2 OR SE)"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testProtectedWords
specifier|public
name|void
name|testProtectedWords
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"70"
argument_list|,
literal|"protectedsubword"
argument_list|,
literal|"c# c++ .net Java/J2SE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"java found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(java)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|".net found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(.net)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c# found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(c#)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c++ found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(c++)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c found?"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:c"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"net found?"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:net"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
