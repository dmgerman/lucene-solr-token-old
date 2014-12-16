begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|SimplePostTool
operator|.
name|PageFetcher
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
name|util
operator|.
name|SimplePostTool
operator|.
name|PageFetcherResult
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
name|Test
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|HashSet
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
begin_comment
comment|/**  * NOTE: do *not* use real hostnames, not even "example.com", in this test.  *  * Even though a MockPageFetcher is used to prevent real HTTP requests from being   * executed, the use of the URL class in SimplePostTool causes attempted resolution of   * the hostnames.  */
end_comment
begin_class
DECL|class|SimplePostToolTest
specifier|public
class|class
name|SimplePostToolTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|t_file
DECL|field|t_file_auto
DECL|field|t_file_rec
DECL|field|t_web
DECL|field|t_test
name|SimplePostTool
name|t_file
decl_stmt|,
name|t_file_auto
decl_stmt|,
name|t_file_rec
decl_stmt|,
name|t_web
decl_stmt|,
name|t_test
decl_stmt|;
DECL|field|pf
name|PageFetcher
name|pf
decl_stmt|;
annotation|@
name|Before
DECL|method|initVariousPostTools
specifier|public
name|void
name|initVariousPostTools
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-"
block|}
decl_stmt|;
comment|// Add a dummy core/collection property so that the SimplePostTool
comment|// doesn't fail fast.
name|System
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|"testcollection"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"data"
argument_list|,
literal|"files"
argument_list|)
expr_stmt|;
name|t_file
operator|=
name|SimplePostTool
operator|.
name|parseArgsAndInit
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"auto"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|t_file_auto
operator|=
name|SimplePostTool
operator|.
name|parseArgsAndInit
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"recursive"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|t_file_rec
operator|=
name|SimplePostTool
operator|.
name|parseArgsAndInit
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"data"
argument_list|,
literal|"web"
argument_list|)
expr_stmt|;
name|t_web
operator|=
name|SimplePostTool
operator|.
name|parseArgsAndInit
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"params"
argument_list|,
literal|"param1=foo&param2=bar"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"url"
argument_list|,
literal|"http://user:password@localhost:5150/solr/update"
argument_list|)
expr_stmt|;
name|t_test
operator|=
name|SimplePostTool
operator|.
name|parseArgsAndInit
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|pf
operator|=
operator|new
name|MockPageFetcher
argument_list|()
expr_stmt|;
name|SimplePostTool
operator|.
name|pageFetcher
operator|=
name|pf
expr_stmt|;
name|SimplePostTool
operator|.
name|mockMode
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseArgsAndInit
specifier|public
name|void
name|testParseArgsAndInit
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|t_file
operator|.
name|auto
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|t_file_auto
operator|.
name|auto
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t_file_auto
operator|.
name|recursive
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|999
argument_list|,
name|t_file_rec
operator|.
name|recursive
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|t_file
operator|.
name|commit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|t_file
operator|.
name|optimize
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|t_file
operator|.
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|t_web
operator|.
name|recursive
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|t_web
operator|.
name|delay
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://user:password@localhost:5150/solr/update?param1=foo&param2=bar"
argument_list|,
name|t_test
operator|.
name|solrUrl
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNormalizeUrlEnding
specifier|public
name|void
name|testNormalizeUrlEnding
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"http://[ff01::114]"
argument_list|,
name|SimplePostTool
operator|.
name|normalizeUrlEnding
argument_list|(
literal|"http://[ff01::114]/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://[ff01::114]"
argument_list|,
name|SimplePostTool
operator|.
name|normalizeUrlEnding
argument_list|(
literal|"http://[ff01::114]/#foo?bar=baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://[ff01::114]/index.html"
argument_list|,
name|SimplePostTool
operator|.
name|normalizeUrlEnding
argument_list|(
literal|"http://[ff01::114]/index.html#hello"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComputeFullUrl
specifier|public
name|void
name|testComputeFullUrl
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|assertEquals
argument_list|(
literal|"http://[ff01::114]/index.html"
argument_list|,
name|t_web
operator|.
name|computeFullUrl
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/"
argument_list|)
argument_list|,
literal|"/index.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://[ff01::114]/index.html"
argument_list|,
name|t_web
operator|.
name|computeFullUrl
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/foo/bar/"
argument_list|)
argument_list|,
literal|"/index.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://[ff01::114]/fil.html"
argument_list|,
name|t_web
operator|.
name|computeFullUrl
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/foo.htm?baz#hello"
argument_list|)
argument_list|,
literal|"fil.html"
argument_list|)
argument_list|)
expr_stmt|;
comment|//    TODO: How to know what is the base if URL path ends with "foo"??
comment|//    assertEquals("http://[ff01::114]/fil.html", t_web.computeFullUrl(new URL("http://[ff01::114]/foo?baz#hello"), "fil.html"));
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|t_web
operator|.
name|computeFullUrl
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/"
argument_list|)
argument_list|,
literal|"fil.jpg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|t_web
operator|.
name|computeFullUrl
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/"
argument_list|)
argument_list|,
literal|"mailto:hello@foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|t_web
operator|.
name|computeFullUrl
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/"
argument_list|)
argument_list|,
literal|"ftp://server/file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTypeSupported
specifier|public
name|void
name|testTypeSupported
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|t_web
operator|.
name|typeSupported
argument_list|(
literal|"application/pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t_web
operator|.
name|typeSupported
argument_list|(
literal|"text/xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t_web
operator|.
name|typeSupported
argument_list|(
literal|"text/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|t_web
operator|.
name|fileTypes
operator|=
literal|"doc,xls,ppt"
expr_stmt|;
name|t_web
operator|.
name|globFileFilter
operator|=
name|t_web
operator|.
name|getFileFilterFromFileTypes
argument_list|(
name|t_web
operator|.
name|fileTypes
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t_web
operator|.
name|typeSupported
argument_list|(
literal|"application/pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t_web
operator|.
name|typeSupported
argument_list|(
literal|"application/msword"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsOn
specifier|public
name|void
name|testIsOn
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|SimplePostTool
operator|.
name|isOn
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SimplePostTool
operator|.
name|isOn
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SimplePostTool
operator|.
name|isOn
argument_list|(
literal|"off"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppendParam
specifier|public
name|void
name|testAppendParam
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"http://[ff01::114]?foo=bar"
argument_list|,
name|SimplePostTool
operator|.
name|appendParam
argument_list|(
literal|"http://[ff01::114]"
argument_list|,
literal|"foo=bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://[ff01::114]/?a=b&foo=bar"
argument_list|,
name|SimplePostTool
operator|.
name|appendParam
argument_list|(
literal|"http://[ff01::114]/?a=b"
argument_list|,
literal|"foo=bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppendUrlPath
specifier|public
name|void
name|testAppendUrlPath
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|assertEquals
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/a?foo=bar"
argument_list|)
argument_list|,
name|SimplePostTool
operator|.
name|appendUrlPath
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]?foo=bar"
argument_list|)
argument_list|,
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGuessType
specifier|public
name|void
name|testGuessType
parameter_list|()
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"foo.doc"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/msword"
argument_list|,
name|SimplePostTool
operator|.
name|guessType
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|SimplePostTool
operator|.
name|guessType
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDoFilesMode
specifier|public
name|void
name|testDoFilesMode
parameter_list|()
block|{
name|t_file_auto
operator|.
name|recursive
operator|=
literal|0
expr_stmt|;
name|File
name|dir
init|=
name|getFile
argument_list|(
literal|"exampledocs"
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|t_file_auto
operator|.
name|postFiles
argument_list|(
operator|new
name|File
index|[]
block|{
name|dir
block|}
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDoWebMode
specifier|public
name|void
name|testDoWebMode
parameter_list|()
block|{
comment|// Uses mock pageFetcher
name|t_web
operator|.
name|delay
operator|=
literal|0
expr_stmt|;
name|t_web
operator|.
name|recursive
operator|=
literal|5
expr_stmt|;
name|int
name|num
init|=
name|t_web
operator|.
name|postWebPages
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"http://[ff01::114]/#removeme"
block|}
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|t_web
operator|.
name|recursive
operator|=
literal|1
expr_stmt|;
name|num
operator|=
name|t_web
operator|.
name|postWebPages
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"http://[ff01::114]/"
block|}
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|num
argument_list|)
expr_stmt|;
comment|// Without respecting robots.txt
name|SimplePostTool
operator|.
name|pageFetcher
operator|.
name|robotsCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|t_web
operator|.
name|recursive
operator|=
literal|5
expr_stmt|;
name|num
operator|=
name|t_web
operator|.
name|postWebPages
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"http://[ff01::114]/#removeme"
block|}
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRobotsExclusion
specifier|public
name|void
name|testRobotsExclusion
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|assertFalse
argument_list|(
name|SimplePostTool
operator|.
name|pageFetcher
operator|.
name|isDisallowedByRobots
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SimplePostTool
operator|.
name|pageFetcher
operator|.
name|isDisallowedByRobots
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/disallowed"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"There should be two entries parsed from robots.txt"
argument_list|,
name|SimplePostTool
operator|.
name|pageFetcher
operator|.
name|robotsCache
operator|.
name|get
argument_list|(
literal|"[ff01::114]"
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|class|MockPageFetcher
specifier|static
class|class
name|MockPageFetcher
extends|extends
name|PageFetcher
block|{
DECL|field|htmlMap
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|htmlMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|linkMap
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|URL
argument_list|>
argument_list|>
name|linkMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MockPageFetcher
specifier|public
name|MockPageFetcher
parameter_list|()
throws|throws
name|IOException
block|{
operator|(
operator|new
name|SimplePostTool
argument_list|()
operator|)
operator|.
name|super
argument_list|()
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/page1\">page1</a><a href=\"http://[ff01::114]/page2\">page2</a></body></html>"
argument_list|)
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/index.html"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/page1\">page1</a><a href=\"http://[ff01::114]/page2\">page2</a></body></html>"
argument_list|)
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page1"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/page1/foo\"></body></html>"
argument_list|)
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page1/foo"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/page1/foo/bar\"></body></html>"
argument_list|)
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page1/foo/bar"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/page1\"></body></html>"
argument_list|)
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page2"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/\"><a href=\"http://[ff01::114]/disallowed\"/></body></html>"
argument_list|)
expr_stmt|;
name|htmlMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/disallowed"
argument_list|,
literal|"<html><body><a href=\"http://[ff01::114]/\"></body></html>"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|URL
argument_list|>
name|s
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/page1"
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/page2"
argument_list|)
argument_list|)
expr_stmt|;
name|linkMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|linkMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/index.html"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/page1/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|linkMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page1"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/page1/foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|linkMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page1/foo"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://[ff01::114]/disallowed"
argument_list|)
argument_list|)
expr_stmt|;
name|linkMap
operator|.
name|put
argument_list|(
literal|"http://[ff01::114]/page2"
argument_list|,
name|s
argument_list|)
expr_stmt|;
comment|// Simulate a robots.txt file with comments and a few disallows
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"# Comments appear after the \"#\" symbol at the start of a line, or after a directive\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"User-agent: * # match all bots\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Disallow:  # This is void\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Disallow: /disallow # Disallow this path\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Disallow: /nonexistingpath # Disallow this path\n"
argument_list|)
expr_stmt|;
name|this
operator|.
name|robotsCache
operator|.
name|put
argument_list|(
literal|"[ff01::114]"
argument_list|,
name|SimplePostTool
operator|.
name|pageFetcher
operator|.
name|parseRobotsTxt
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readPageFromUrl
specifier|public
name|PageFetcherResult
name|readPageFromUrl
parameter_list|(
name|URL
name|u
parameter_list|)
block|{
name|PageFetcherResult
name|res
init|=
operator|(
operator|new
name|SimplePostTool
argument_list|()
operator|)
operator|.
operator|new
name|PageFetcherResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|isDisallowedByRobots
argument_list|(
name|u
argument_list|)
condition|)
block|{
name|res
operator|.
name|httpStatus
operator|=
literal|403
expr_stmt|;
return|return
name|res
return|;
block|}
name|res
operator|.
name|httpStatus
operator|=
literal|200
expr_stmt|;
name|res
operator|.
name|contentType
operator|=
literal|"text/html"
expr_stmt|;
name|res
operator|.
name|content
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|htmlMap
operator|.
name|get
argument_list|(
name|u
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|getLinksFromWebPage
specifier|public
name|Set
argument_list|<
name|URL
argument_list|>
name|getLinksFromWebPage
parameter_list|(
name|URL
name|u
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|String
name|type
parameter_list|,
name|URL
name|postUrl
parameter_list|)
block|{
name|Set
argument_list|<
name|URL
argument_list|>
name|s
init|=
name|linkMap
operator|.
name|get
argument_list|(
name|SimplePostTool
operator|.
name|normalizeUrlEnding
argument_list|(
name|u
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
name|s
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
block|}
end_class
end_unit
