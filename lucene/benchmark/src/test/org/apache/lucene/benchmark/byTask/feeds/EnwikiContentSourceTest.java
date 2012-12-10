begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ByteArrayOutputStream
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|IOUtils
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
name|LuceneTestCase
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
DECL|class|EnwikiContentSourceTest
specifier|public
class|class
name|EnwikiContentSourceTest
extends|extends
name|LuceneTestCase
block|{
comment|/** An EnwikiContentSource which works on a String and not files. */
DECL|class|StringableEnwikiSource
specifier|private
specifier|static
class|class
name|StringableEnwikiSource
extends|extends
name|EnwikiContentSource
block|{
DECL|field|docs
specifier|private
specifier|final
name|String
name|docs
decl_stmt|;
DECL|method|StringableEnwikiSource
specifier|public
name|StringableEnwikiSource
parameter_list|(
name|String
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInputStream
specifier|protected
name|InputStream
name|openInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|docs
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|assertDocData
specifier|private
name|void
name|assertDocData
parameter_list|(
name|DocData
name|dd
parameter_list|,
name|String
name|expName
parameter_list|,
name|String
name|expTitle
parameter_list|,
name|String
name|expBody
parameter_list|,
name|String
name|expDate
parameter_list|)
throws|throws
name|ParseException
block|{
name|assertNotNull
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expName
argument_list|,
name|dd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTitle
argument_list|,
name|dd
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expBody
argument_list|,
name|dd
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expDate
argument_list|,
name|dd
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoMoreDataException
specifier|private
name|void
name|assertNoMoreDataException
parameter_list|(
name|EnwikiContentSource
name|stdm
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|stdm
operator|.
name|getNextDocData
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting NoMoreDataException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|field|PAGE1
specifier|private
specifier|final
name|String
name|PAGE1
init|=
literal|"<page>\r\n"
operator|+
literal|"<title>Title1</title>\r\n"
operator|+
literal|"<ns>0</ns>\r\n"
operator|+
literal|"<id>1</id>\r\n"
operator|+
literal|"<revision>\r\n"
operator|+
literal|"<id>11</id>\r\n"
operator|+
literal|"<parentid>111</parentid>\r\n"
operator|+
literal|"<timestamp>2011-09-14T11:35:09Z</timestamp>\r\n"
operator|+
literal|"<contributor>\r\n"
operator|+
literal|"<username>Mister1111</username>\r\n"
operator|+
literal|"<id>1111</id>\r\n"
operator|+
literal|"</contributor>\r\n"
operator|+
literal|"<minor />\r\n"
operator|+
literal|"<comment>/* Never mind */</comment>\r\n"
operator|+
literal|"<text>Some text 1 here</text>\r\n"
operator|+
literal|"</revision>\r\n"
operator|+
literal|"</page>\r\n"
decl_stmt|;
DECL|field|PAGE2
specifier|private
specifier|final
name|String
name|PAGE2
init|=
literal|"<page>\r\n"
operator|+
literal|"<title>Title2</title>\r\n"
operator|+
literal|"<ns>0</ns>\r\n"
operator|+
literal|"<id>2</id>\r\n"
operator|+
literal|"<revision>\r\n"
operator|+
literal|"<id>22</id>\r\n"
operator|+
literal|"<parentid>222</parentid>\r\n"
operator|+
literal|"<timestamp>2022-09-14T22:35:09Z</timestamp>\r\n"
operator|+
literal|"<contributor>\r\n"
operator|+
literal|"<username>Mister2222</username>\r\n"
operator|+
literal|"<id>2222</id>\r\n"
operator|+
literal|"</contributor>\r\n"
operator|+
literal|"<minor />\r\n"
operator|+
literal|"<comment>/* Never mind */</comment>\r\n"
operator|+
literal|"<text>Some text 2 here</text>\r\n"
operator|+
literal|"</revision>\r\n"
operator|+
literal|"</page>\r\n"
decl_stmt|;
annotation|@
name|Test
DECL|method|testOneDocument
specifier|public
name|void
name|testOneDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<mediawiki>\r\n"
operator|+
name|PAGE1
operator|+
literal|"</mediawiki>"
decl_stmt|;
name|EnwikiContentSource
name|source
init|=
name|createContentSource
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
decl_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"1"
argument_list|,
literal|"Title1"
argument_list|,
literal|"Some text 1 here"
argument_list|,
literal|"14-SEP-2011 11:35:09.000"
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
DECL|method|createContentSource
specifier|private
name|EnwikiContentSource
name|createContentSource
parameter_list|(
name|String
name|docs
parameter_list|,
name|boolean
name|forever
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"print.props"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"content.source.forever"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|forever
argument_list|)
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|EnwikiContentSource
name|source
init|=
operator|new
name|StringableEnwikiSource
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// doc-maker just for initiating content source inputs
name|DocMaker
name|docMaker
init|=
operator|new
name|DocMaker
argument_list|()
decl_stmt|;
name|docMaker
operator|.
name|setConfig
argument_list|(
name|config
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|docMaker
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
return|return
name|source
return|;
block|}
annotation|@
name|Test
DECL|method|testTwoDocuments
specifier|public
name|void
name|testTwoDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<mediawiki>\r\n"
operator|+
name|PAGE1
operator|+
name|PAGE2
operator|+
literal|"</mediawiki>"
decl_stmt|;
name|EnwikiContentSource
name|source
init|=
name|createContentSource
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocData
name|dd1
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
decl_stmt|;
name|assertDocData
argument_list|(
name|dd1
argument_list|,
literal|"1"
argument_list|,
literal|"Title1"
argument_list|,
literal|"Some text 1 here"
argument_list|,
literal|"14-SEP-2011 11:35:09.000"
argument_list|)
expr_stmt|;
name|DocData
name|dd2
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
decl_stmt|;
name|assertDocData
argument_list|(
name|dd2
argument_list|,
literal|"2"
argument_list|,
literal|"Title2"
argument_list|,
literal|"Some text 2 here"
argument_list|,
literal|"14-SEP-2022 22:35:09.000"
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testForever
specifier|public
name|void
name|testForever
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<mediawiki>\r\n"
operator|+
name|PAGE1
operator|+
name|PAGE2
operator|+
literal|"</mediawiki>"
decl_stmt|;
name|EnwikiContentSource
name|source
init|=
name|createContentSource
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// same documents several times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|DocData
name|dd1
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
decl_stmt|;
name|assertDocData
argument_list|(
name|dd1
argument_list|,
literal|"1"
argument_list|,
literal|"Title1"
argument_list|,
literal|"Some text 1 here"
argument_list|,
literal|"14-SEP-2011 11:35:09.000"
argument_list|)
expr_stmt|;
name|DocData
name|dd2
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
decl_stmt|;
name|assertDocData
argument_list|(
name|dd2
argument_list|,
literal|"2"
argument_list|,
literal|"Title2"
argument_list|,
literal|"Some text 2 here"
argument_list|,
literal|"14-SEP-2022 22:35:09.000"
argument_list|)
expr_stmt|;
comment|// Don't test that NoMoreDataException is thrown, since the forever flag is turned on.
block|}
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
