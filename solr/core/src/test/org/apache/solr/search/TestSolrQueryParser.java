begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|search
operator|.
name|ConstantScoreQuery
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
name|core
operator|.
name|SolrInfoMBean
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
name|response
operator|.
name|transform
operator|.
name|ScoreAugmenter
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
begin_class
DECL|class|TestSolrQueryParser
specifier|public
class|class
name|TestSolrQueryParser
extends|extends
name|SolrTestCaseJ4
block|{
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
name|String
name|v
decl_stmt|;
name|v
operator|=
literal|"how now brown cow"
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
name|v
argument_list|,
literal|"text_np"
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|=
literal|"now cow"
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
name|v
argument_list|,
literal|"text_np"
argument_list|,
name|v
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
literal|"foo_s"
argument_list|,
literal|"a ' \" \\ {! ) } ( { z"
argument_list|)
argument_list|)
expr_stmt|;
comment|// A value filled with special chars
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"qqq_s"
argument_list|,
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"www_s"
argument_list|,
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"eee_s"
argument_list|,
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"eee_s"
argument_list|,
literal|"'balance'"
argument_list|)
argument_list|)
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
DECL|method|testPhrase
specifier|public
name|void
name|testPhrase
parameter_list|()
block|{
comment|// should generate a phrase of "now cow" and match only one doc
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:now-cow"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// should generate a query of (now OR cow) and match both docs
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text_np:now-cow"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocalParamsInQP
specifier|public
name|void
name|testLocalParamsInQP
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=text v=$qq} wsx"
argument_list|,
literal|"qq"
argument_list|,
literal|"now"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=text v=$qq} wsx"
argument_list|,
literal|"qq"
argument_list|,
literal|"nomatch"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=text}now wsx"
argument_list|,
literal|"qq"
argument_list|,
literal|"now"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=foo_s v='a \\' \" \\\\ {! ) } ( { z'} wsx"
argument_list|)
comment|// single quote escaping
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=foo_s v=\"a ' \\\" \\\\ {! ) } ( { z\"} wsx"
argument_list|)
comment|// double quote escaping
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
comment|// double-join to test back-to-back local params
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!join from=www_s to=eee_s}{!join from=qqq_s to=www_s}id:10"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/id=='12'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSolr4121
specifier|public
name|void
name|testSolr4121
parameter_list|()
throws|throws
name|Exception
block|{
comment|// At one point, balanced quotes messed up the parser(SOLR-4121)
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"eee_s:'balance'"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSyntax
specifier|public
name|void
name|testSyntax
parameter_list|()
throws|throws
name|Exception
block|{
comment|// a bare * should be treated as *:*
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*"
argument_list|,
literal|"df"
argument_list|,
literal|"doesnotexist_s"
argument_list|)
argument_list|,
literal|"/response/docs/[0]=="
comment|// make sure we get something...
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doesnotexist_s:*"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
comment|// nothing should be found
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doesnotexist_s:( * * * )"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
comment|// nothing should be found
argument_list|)
expr_stmt|;
comment|// length of date math caused issues...
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo_dt:\"2013-03-08T00:46:15Z/DAY+000MILLISECONDS+00SECONDS+00MINUTES+00HOURS+0000000000YEARS+6MONTHS+3DAYS\""
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/debug/parsedquery=='foo_dt:2013-09-11T00:00:00Z'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedQueryModifiers
specifier|public
name|void
name|testNestedQueryModifiers
parameter_list|()
throws|throws
name|Exception
block|{
comment|// One previous error was that for nested queries, outer parameters overrode nested parameters.
comment|// For example _query_:"\"a b\"~2" was parsed as "a b"
name|String
name|subqq
init|=
literal|"_query_:\"{!v=$qq}\""
decl_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_query_:\"\\\"how brown\\\"~2\""
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|subqq
argument_list|,
literal|"qq"
argument_list|,
literal|"\"how brown\"~2"
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
comment|// Should explicit slop override?  It currently does not, but that could be considered a bug.
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|subqq
operator|+
literal|"~1"
argument_list|,
literal|"qq"
argument_list|,
literal|"\"how brown\"~2"
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
comment|// Should explicit slop override?  It currently does not, but that could be considered a bug.
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"  {!v=$qq}~1"
argument_list|,
literal|"qq"
argument_list|,
literal|"\"how brown\"~2"
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
comment|// boost should multiply
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"q"
argument_list|,
name|subqq
operator|+
literal|"^3"
argument_list|,
literal|"qq"
argument_list|,
literal|"text:x^2"
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/debug/parsedquery=='text:x^6.0'"
argument_list|)
expr_stmt|;
comment|// boost should multiply
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"q"
argument_list|,
literal|"  {!v=$qq}^3"
argument_list|,
literal|"qq"
argument_list|,
literal|"text:x^2"
argument_list|,
literal|"debug"
argument_list|,
literal|"query"
argument_list|)
argument_list|,
literal|"/debug/parsedquery=='text:x^6.0'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCSQ
specifier|public
name|void
name|testCSQ
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|QParser
name|qParser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
literal|"text:x^=3"
argument_list|,
literal|"lucene"
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qParser
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|ConstantScoreQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|qParser
operator|=
name|QParser
operator|.
name|getParser
argument_list|(
literal|"(text:x text:y)^=-3"
argument_list|,
literal|"lucene"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|q
operator|=
name|qParser
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|ConstantScoreQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|3.0
argument_list|,
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComments
specifier|public
name|void
name|testComments
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1 id:2 /* *:* */ id:3"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
comment|//
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1 /**.*/"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
comment|// if it matches more than one, it's being treated as a regex.
argument_list|)
expr_stmt|;
comment|// don't match comment start in string
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|" \"/*\" id:1 id:2 \"*/\" id:3"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
comment|// don't match an end of comment within  a string
comment|// assertJQ(req("q","id:1 id:2 /* \"*/\" *:* */ id:3")
comment|//     ,"/response/numFound==3"
comment|// );
comment|// removed this functionality - there's more of a danger to thinking we're in a string.
comment|//   can't do it */  ......... '
comment|// nested comments
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1 /* id:2 /* */ /* /**/ id:3 */ id:10 */ id:11"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// normal test "solrconfig.xml" has autowarm set to 2...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:* "
operator|+
name|i
argument_list|,
literal|"fq"
argument_list|,
literal|"filter(just_to_clear_the_cache) filter(id:10000"
operator|+
name|i
operator|+
literal|") filter(id:10001"
operator|+
name|i
operator|+
literal|")"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"777"
argument_list|)
argument_list|)
expr_stmt|;
name|delI
argument_list|(
literal|"777"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// arg... commit no longer "commits" unless there has been a change.
specifier|final
name|SolrInfoMBean
name|filterCacheStats
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"filterCache"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|filterCacheStats
argument_list|)
expr_stmt|;
specifier|final
name|SolrInfoMBean
name|queryCacheStats
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"queryResultCache"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queryCacheStats
argument_list|)
expr_stmt|;
name|long
name|inserts
init|=
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
decl_stmt|;
name|long
name|hits
init|=
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
decl_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doesnotexist filter(id:1) filter(qqq_s:X) filter(abcdefg)"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|inserts
operator|+=
literal|3
expr_stmt|;
name|assertEquals
argument_list|(
name|inserts
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doesnotexist2 filter(id:1) filter(qqq_s:X) filter(abcdefg)"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|hits
operator|+=
literal|3
expr_stmt|;
name|assertEquals
argument_list|(
name|inserts
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure normal "fq" parameters also hit the cache the same way
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doesnotexist3"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fq"
argument_list|,
literal|"qqq_s:X"
argument_list|,
literal|"fq"
argument_list|,
literal|"abcdefg"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|hits
operator|+=
literal|3
expr_stmt|;
name|assertEquals
argument_list|(
name|inserts
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// try a query deeply nested in a FQ
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:* doesnotexist4"
argument_list|,
literal|"fq"
argument_list|,
literal|"(id:* +(filter(id:1) filter(qqq_s:X) filter(abcdefg)) )"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|inserts
operator|+=
literal|1
expr_stmt|;
comment|// +1 for top level fq
name|hits
operator|+=
literal|3
expr_stmt|;
name|assertEquals
argument_list|(
name|inserts
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// retry the complex FQ and make sure hashCode/equals works as expected w/ filter queries
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:* doesnotexist5"
argument_list|,
literal|"fq"
argument_list|,
literal|"(id:* +(filter(id:1) filter(qqq_s:X) filter(abcdefg)) )"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|hits
operator|+=
literal|1
expr_stmt|;
comment|// top-level fq should have been found.
name|assertEquals
argument_list|(
name|inserts
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// try nested filter with multiple top-level args (i.e. a boolean query)
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:* +filter(id:1 filter(qqq_s:X) abcdefg)"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|hits
operator|+=
literal|1
expr_stmt|;
comment|// the inner filter
name|inserts
operator|+=
literal|1
expr_stmt|;
comment|// the outer filter
name|assertEquals
argument_list|(
name|inserts
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|filterCacheStats
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// test the score for a filter, and that default score is 0
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"+filter(*:*) +filter(id:1)"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==0.0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"+filter(*:*)^=10 +filter(id:1)"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==1.0"
comment|// normalization reduces to 1
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
