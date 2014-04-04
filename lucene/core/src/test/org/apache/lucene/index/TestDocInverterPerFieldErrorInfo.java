begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|MockAnalyzer
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
name|MockTokenizer
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
name|TokenFilter
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
name|FieldType
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
name|TextField
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
name|store
operator|.
name|Directory
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PrintStreamInfoStream
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
name|PrintStream
import|;
end_import
begin_comment
comment|/**  * Test adding to the info stream when there's an exception thrown during field analysis.  */
end_comment
begin_class
DECL|class|TestDocInverterPerFieldErrorInfo
specifier|public
class|class
name|TestDocInverterPerFieldErrorInfo
extends|extends
name|LuceneTestCase
block|{
DECL|field|storedTextType
specifier|private
specifier|static
specifier|final
name|FieldType
name|storedTextType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
DECL|class|BadNews
specifier|private
specifier|static
class|class
name|BadNews
extends|extends
name|RuntimeException
block|{
DECL|method|BadNews
specifier|private
name|BadNews
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ThrowingAnalyzer
specifier|private
specifier|static
class|class
name|ThrowingAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"distinctiveFieldName"
argument_list|)
condition|)
block|{
name|TokenFilter
name|tosser
init|=
operator|new
name|TokenFilter
argument_list|(
name|tokenizer
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|BadNews
argument_list|(
literal|"Something is icky."
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tosser
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testInfoStreamGetsFieldName
specifier|public
name|void
name|testInfoStreamGetsFieldName
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
decl_stmt|;
name|IndexWriterConfig
name|c
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|ThrowingAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ByteArrayOutputStream
name|infoBytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|infoPrintStream
init|=
operator|new
name|PrintStream
argument_list|(
name|infoBytes
argument_list|,
literal|true
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|PrintStreamInfoStream
name|printStreamInfoStream
init|=
operator|new
name|PrintStreamInfoStream
argument_list|(
name|infoPrintStream
argument_list|)
decl_stmt|;
name|c
operator|.
name|setInfoStream
argument_list|(
name|printStreamInfoStream
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"distinctiveFieldName"
argument_list|,
literal|"aaa "
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadNews
name|badNews
parameter_list|)
block|{
name|infoPrintStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|infoStream
init|=
operator|new
name|String
argument_list|(
name|infoBytes
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|infoStream
operator|.
name|contains
argument_list|(
literal|"distinctiveFieldName"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoExtraNoise
specifier|public
name|void
name|testNoExtraNoise
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
decl_stmt|;
name|IndexWriterConfig
name|c
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|ThrowingAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ByteArrayOutputStream
name|infoBytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|infoPrintStream
init|=
operator|new
name|PrintStream
argument_list|(
name|infoBytes
argument_list|,
literal|true
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|PrintStreamInfoStream
name|printStreamInfoStream
init|=
operator|new
name|PrintStreamInfoStream
argument_list|(
name|infoPrintStream
argument_list|)
decl_stmt|;
name|c
operator|.
name|setInfoStream
argument_list|(
name|printStreamInfoStream
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"boringFieldName"
argument_list|,
literal|"aaa "
argument_list|,
name|storedTextType
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadNews
name|badNews
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unwanted exception"
argument_list|)
expr_stmt|;
block|}
name|infoPrintStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|infoStream
init|=
operator|new
name|String
argument_list|(
name|infoBytes
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|infoStream
operator|.
name|contains
argument_list|(
literal|"boringFieldName"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
