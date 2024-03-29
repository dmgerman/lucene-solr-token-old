begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
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
name|search
operator|.
name|AutomatonQuery
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
name|search
operator|.
name|Query
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
name|util
operator|.
name|automaton
operator|.
name|Automaton
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
name|util
operator|.
name|automaton
operator|.
name|Operations
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchemaFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrQueryParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|BaseTokenStreamTestCase
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestReversedWildcardFilterFactory
specifier|public
class|class
name|TestReversedWildcardFilterFactory
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|args
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|schema
name|IndexSchema
name|schema
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema-reversed.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|schema
operator|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|,
name|solrConfig
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReversedTokens
specifier|public
name|void
name|testReversedTokens
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|text
init|=
literal|"simple text"
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"withOriginal"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|ReversedWildcardFilterFactory
name|factory
init|=
operator|new
name|ReversedWildcardFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|TokenStream
name|input
init|=
name|factory
operator|.
name|create
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u0001elpmis"
block|,
literal|"simple"
block|,
literal|"\u0001txet"
block|,
literal|"text"
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
literal|0
block|}
argument_list|)
expr_stmt|;
comment|// now without original tokens
name|args
operator|.
name|put
argument_list|(
literal|"withOriginal"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|ReversedWildcardFilterFactory
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|input
operator|=
name|factory
operator|.
name|create
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u0001elpmis"
block|,
literal|"\u0001txet"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndexingAnalysis
specifier|public
name|void
name|testIndexingAnalysis
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
name|schema
operator|.
name|getIndexAnalyzer
argument_list|()
decl_stmt|;
name|String
name|text
init|=
literal|"one two three si\uD834\uDD1Ex"
decl_stmt|;
comment|// field one
name|TokenStream
name|input
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"one"
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u0001eno"
block|,
literal|"one"
block|,
literal|"\u0001owt"
block|,
literal|"two"
block|,
literal|"\u0001eerht"
block|,
literal|"three"
block|,
literal|"\u0001x\uD834\uDD1Eis"
block|,
literal|"si\uD834\uDD1Ex"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|4
block|,
literal|4
block|,
literal|8
block|,
literal|8
block|,
literal|14
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|3
block|,
literal|7
block|,
literal|7
block|,
literal|13
block|,
literal|13
block|,
literal|19
block|,
literal|19
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
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
comment|// field two
name|input
operator|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"two"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u0001eno"
block|,
literal|"\u0001owt"
block|,
literal|"\u0001eerht"
block|,
literal|"\u0001x\uD834\uDD1Eis"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|,
literal|8
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|7
block|,
literal|13
block|,
literal|19
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
block|}
argument_list|)
expr_stmt|;
comment|// field three
name|input
operator|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"three"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|,
literal|"si\uD834\uDD1Ex"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|,
literal|8
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|7
block|,
literal|13
block|,
literal|19
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryParsing
specifier|public
name|void
name|testQueryParsing
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add some docs
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"one"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"two"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"three"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"one"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"two"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"three"
argument_list|,
literal|"si\uD834\uDD1Ex"
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
literal|"should have matched"
argument_list|,
name|req
argument_list|(
literal|"+id:1 +one:one"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should have matched"
argument_list|,
name|req
argument_list|(
literal|"+id:4 +one:f*ur"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should have matched"
argument_list|,
name|req
argument_list|(
literal|"+id:6 +three:*si\uD834\uDD1Ex"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|QParser
name|qparser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
literal|"id:1"
argument_list|,
literal|"lucene"
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|SolrQueryParser
name|parserTwo
init|=
operator|new
name|SolrQueryParser
argument_list|(
name|qparser
argument_list|,
literal|"two"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|parserTwo
operator|.
name|getAllowLeadingWildcard
argument_list|()
argument_list|)
expr_stmt|;
comment|// test conditional reversal
name|assertTrue
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"*hree"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"t*ree"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"th*ee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"thr*e"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"?hree"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"t?ree"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"th?ee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"th?*ee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"short*token"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|wasReversed
argument_list|(
name|parserTwo
argument_list|,
literal|"ver*longtoken"
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** fragile assert: depends on our implementation, but cleanest way to check for now */
DECL|method|wasReversed
specifier|private
name|boolean
name|wasReversed
parameter_list|(
name|SolrQueryParser
name|qp
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|AutomatonQuery
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Automaton
name|automaton
init|=
operator|(
operator|(
name|AutomatonQuery
operator|)
name|q
operator|)
operator|.
name|getAutomaton
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|Operations
operator|.
name|getCommonPrefix
argument_list|(
name|Operations
operator|.
name|determinize
argument_list|(
name|automaton
argument_list|,
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|prefix
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'\u0001'
return|;
block|}
annotation|@
name|Test
DECL|method|testFalsePositives
specifier|public
name|void
name|testFalsePositives
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add a doc
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"one"
argument_list|,
literal|"gomez"
argument_list|,
literal|"two"
argument_list|,
literal|"gomez"
argument_list|,
literal|"three"
argument_list|,
literal|"gomez"
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
literal|"false positive"
argument_list|,
name|req
argument_list|(
literal|"+id:1 +one:*zemog*"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"false positive"
argument_list|,
name|req
argument_list|(
literal|"+id:1 +two:*zemog*"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"false positive"
argument_list|,
name|req
argument_list|(
literal|"+id:1 +three:*zemog*"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should have matched"
argument_list|,
name|req
argument_list|(
literal|"+id:1 +one:*omez*"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
