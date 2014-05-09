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
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|CrankyTokenFilter
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
name|MockVariableLengthPayloadFilter
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|asserting
operator|.
name|AssertingCodec
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
name|codecs
operator|.
name|cranky
operator|.
name|CrankyCodec
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
name|BinaryDocValuesField
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
name|NumericDocValuesField
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
name|SortedDocValuesField
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
name|SortedSetDocValuesField
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
name|StoredField
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
name|store
operator|.
name|MockDirectoryWrapper
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
name|BytesRef
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
name|TestUtil
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
name|Rethrow
import|;
end_import
begin_comment
comment|/**   * Causes a bunch of non-aborting and aborting exceptions and checks that  * no index corruption is ever created  */
end_comment
begin_class
DECL|class|TestIndexWriterExceptions2
specifier|public
class|class
name|TestIndexWriterExceptions2
extends|extends
name|LuceneTestCase
block|{
comment|// just one thread, serial merge policy, hopefully debuggable
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
comment|// disable slow things: we don't rely upon sleeps here.
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setUseSlowOpenClosers
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// log all exceptions we hit, in case we fail (for debugging)
name|ByteArrayOutputStream
name|exceptionLog
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|exceptionStream
init|=
operator|new
name|PrintStream
argument_list|(
name|exceptionLog
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|//PrintStream exceptionStream = System.out;
comment|// create lots of non-aborting exceptions with a broken analyzer
specifier|final
name|long
name|analyzerSeed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
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
parameter_list|)
block|{
name|MockTokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// TODO: can we turn this on? our filter is probably too evil
name|TokenStream
name|stream
init|=
name|tokenizer
decl_stmt|;
comment|// emit some payloads
if|if
condition|(
name|fieldName
operator|.
name|contains
argument_list|(
literal|"payloads"
argument_list|)
condition|)
block|{
name|stream
operator|=
operator|new
name|MockVariableLengthPayloadFilter
argument_list|(
operator|new
name|Random
argument_list|(
name|analyzerSeed
argument_list|)
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
name|stream
operator|=
operator|new
name|CrankyTokenFilter
argument_list|(
name|stream
argument_list|,
operator|new
name|Random
argument_list|(
name|analyzerSeed
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// create lots of aborting exceptions with a broken codec
comment|// we don't need a random codec, as we aren't trying to find bugs in the codec here.
name|Codec
name|inner
init|=
name|RANDOM_MULTIPLIER
operator|>
literal|1
condition|?
name|Codec
operator|.
name|getDefault
argument_list|()
else|:
operator|new
name|AssertingCodec
argument_list|()
decl_stmt|;
name|Codec
name|codec
init|=
operator|new
name|CrankyCodec
argument_list|(
name|inner
argument_list|,
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
comment|// just for now, try to keep this test reproducible
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
comment|// TODO: add crankyDocValuesFields, etc
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"dv2"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"dv3"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultCodecSupportsSortedSet
argument_list|()
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"dv4"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"dv4"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text1"
argument_list|,
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure we store something
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"stored1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"stored1"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure we get some payloads
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text_payloads"
argument_list|,
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|6
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure we get some vectors
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"text_vectors"
argument_list|,
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|6
argument_list|,
literal|true
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// single doc
try|try
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// we made it, sometimes delete our doc, or update a dv
name|int
name|thingToDo
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thingToDo
operator|==
literal|1
operator|&&
name|defaultCodecSupportsFieldUpdates
argument_list|()
condition|)
block|{
name|iw
operator|.
name|updateNumericDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
literal|"dv"
argument_list|,
name|i
operator|+
literal|1L
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thingToDo
operator|==
literal|2
operator|&&
name|defaultCodecSupportsFieldUpdates
argument_list|()
condition|)
block|{
name|iw
operator|.
name|updateBinaryDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
literal|"dv2"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Fake IOException"
argument_list|)
condition|)
block|{
name|exceptionStream
operator|.
name|println
argument_list|(
literal|"\nTEST: got expected fake exc:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|exceptionStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// block docs
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
operator|-
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text1"
argument_list|,
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"stored1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"stored1"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"text_vectors"
argument_list|,
name|TestUtil
operator|.
name|randomAnalysisString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|6
argument_list|,
literal|true
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|iw
operator|.
name|addDocuments
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|doc
argument_list|,
name|doc2
argument_list|)
argument_list|)
expr_stmt|;
comment|// we made it, sometimes delete our docs
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
operator|-
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Fake IOException"
argument_list|)
condition|)
block|{
name|exceptionStream
operator|.
name|println
argument_list|(
literal|"\nTEST: got expected fake exc:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|exceptionStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// trigger flush:
try|try
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|DirectoryReader
name|ir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|iw
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|ir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|ir
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Fake IOException"
argument_list|)
condition|)
block|{
name|exceptionStream
operator|.
name|println
argument_list|(
literal|"\nTEST: got expected fake exc:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|exceptionStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
try|try
block|{
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Fake IOException"
argument_list|)
condition|)
block|{
name|exceptionStream
operator|.
name|println
argument_list|(
literal|"\nTEST: got expected fake exc:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|exceptionStream
argument_list|)
expr_stmt|;
try|try
block|{
name|iw
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{}
block|}
else|else
block|{
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unexpected exception: dumping fake-exception-log:..."
argument_list|)
expr_stmt|;
name|exceptionStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|exceptionLog
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST PASSED: dumping fake-exception-log:..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|exceptionLog
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit