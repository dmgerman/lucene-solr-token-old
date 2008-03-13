begin_unit
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|*
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
name|RAMDirectory
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
name|*
import|;
end_import
begin_comment
comment|/**  * Asserts equality of content and behaviour of two index readers.  */
end_comment
begin_class
DECL|class|TestIndicesEquals
specifier|public
class|class
name|TestIndicesEquals
extends|extends
name|TestCase
block|{
comment|//  public void test2() throws Exception {
comment|//    FSDirectory fsdir = FSDirectory.getDirectory("/tmp/fatcorpus");
comment|//    IndexReader ir = IndexReader.open(fsdir);
comment|//    InstantiatedIndex ii = new InstantiatedIndex(ir);
comment|//    ir.close();
comment|//    testEquals(fsdir, ii);
comment|//  }
DECL|method|testLoadIndexReader
specifier|public
name|void
name|testLoadIndexReader
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// create dir data
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|assembleDocument
argument_list|(
name|document
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test load ii from index reader
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|testEquals
argument_list|(
name|dir
argument_list|,
name|ii
argument_list|)
expr_stmt|;
block|}
DECL|method|testInstantiatedIndexWriter
specifier|public
name|void
name|testInstantiatedIndexWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
comment|// create dir data
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
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
literal|500
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|assembleDocument
argument_list|(
name|document
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test ii writer
name|InstantiatedIndexWriter
name|instantiatedIndexWriter
init|=
name|ii
operator|.
name|indexWriterFactory
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
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
literal|500
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|assembleDocument
argument_list|(
name|document
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|instantiatedIndexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|instantiatedIndexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|testEquals
argument_list|(
name|dir
argument_list|,
name|ii
argument_list|)
expr_stmt|;
name|testTermDocs
argument_list|(
name|dir
argument_list|,
name|ii
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermDocs
specifier|private
name|void
name|testTermDocs
parameter_list|(
name|Directory
name|aprioriIndex
parameter_list|,
name|InstantiatedIndex
name|testIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|aprioriReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|aprioriIndex
argument_list|)
decl_stmt|;
name|IndexReader
name|testReader
init|=
name|testIndex
operator|.
name|indexReaderFactory
argument_list|()
decl_stmt|;
name|TermEnum
name|aprioriTermEnum
init|=
name|aprioriReader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"c"
argument_list|,
literal|"danny"
argument_list|)
argument_list|)
decl_stmt|;
name|TermDocs
name|aprioriTermDocs
init|=
name|aprioriReader
operator|.
name|termDocs
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
name|TermDocs
name|testTermDocs
init|=
name|testReader
operator|.
name|termDocs
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|next
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|skipTo
argument_list|(
literal|100
argument_list|)
argument_list|,
name|testTermDocs
operator|.
name|skipTo
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|next
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|next
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|skipTo
argument_list|(
literal|110
argument_list|)
argument_list|,
name|testTermDocs
operator|.
name|skipTo
argument_list|(
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|skipTo
argument_list|(
literal|10
argument_list|)
argument_list|,
name|testTermDocs
operator|.
name|skipTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|skipTo
argument_list|(
literal|210
argument_list|)
argument_list|,
name|testTermDocs
operator|.
name|skipTo
argument_list|(
literal|210
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|aprioriTermDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|aprioriReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|testReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assembleDocument
specifier|private
name|void
name|assembleDocument
parameter_list|(
name|Document
name|document
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"a"
argument_list|,
name|i
operator|+
literal|" Do you really want to go and live in that house all winter?"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b0"
argument_list|,
name|i
operator|+
literal|" All work and no play makes Jack a dull boy"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b1"
argument_list|,
name|i
operator|+
literal|" All work and no play makes Jack a dull boy"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO_NORMS
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b2"
argument_list|,
name|i
operator|+
literal|" All work and no play makes Jack a dull boy"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"b3"
argument_list|,
name|i
operator|+
literal|" All work and no play makes Jack a dull boy"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|1
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"c"
argument_list|,
name|i
operator|+
literal|" Redrum redrum"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|2
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"d"
argument_list|,
name|i
operator|+
literal|" Hello Danny, come and play with us... forever and ever. and ever."
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|3
condition|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"e"
argument_list|,
name|i
operator|+
literal|" Heres Johnny!"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
decl_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|4
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
literal|"the"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|Token
argument_list|(
literal|"end"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|2
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|Token
argument_list|(
literal|"fin"
argument_list|,
literal|7
argument_list|,
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
operator|new
name|TokenStream
argument_list|()
block|{
name|Iterator
argument_list|<
name|Token
argument_list|>
name|it
init|=
name|tokens
operator|.
name|iterator
argument_list|()
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
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|it
operator|.
name|next
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|it
operator|=
name|tokens
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**    * Asserts that the content of two index readers equal each other.    *    * @param aprioriIndex the index that is known to be correct    * @param testIndex    the index that is supposed to equals the apriori index.    * @throws Exception    */
DECL|method|testEquals
specifier|protected
name|void
name|testEquals
parameter_list|(
name|Directory
name|aprioriIndex
parameter_list|,
name|InstantiatedIndex
name|testIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|aprioriReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|aprioriIndex
argument_list|)
decl_stmt|;
name|IndexReader
name|testReader
init|=
name|testIndex
operator|.
name|indexReaderFactory
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|aprioriReader
operator|.
name|numDocs
argument_list|()
argument_list|,
name|testReader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|field
range|:
name|aprioriReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
argument_list|)
control|)
block|{
comment|// test norms as used by normal use
name|byte
index|[]
name|aprioriNorms
init|=
name|aprioriReader
operator|.
name|norms
argument_list|(
operator|(
name|String
operator|)
name|field
argument_list|)
decl_stmt|;
name|byte
index|[]
name|testNorms
init|=
name|testReader
operator|.
name|norms
argument_list|(
operator|(
name|String
operator|)
name|field
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|aprioriNorms
operator|.
name|length
argument_list|,
name|testNorms
operator|.
name|length
argument_list|)
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
name|aprioriNorms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"norms does not equals for field "
operator|+
name|field
operator|+
literal|" in document "
operator|+
name|i
argument_list|,
name|aprioriNorms
index|[
name|i
index|]
argument_list|,
name|testNorms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// test norms as used by multireader
name|aprioriNorms
operator|=
operator|new
name|byte
index|[
name|aprioriReader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|aprioriReader
operator|.
name|norms
argument_list|(
operator|(
name|String
operator|)
name|field
argument_list|,
name|aprioriNorms
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testNorms
operator|=
operator|new
name|byte
index|[
name|testReader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|testReader
operator|.
name|norms
argument_list|(
operator|(
name|String
operator|)
name|field
argument_list|,
name|testNorms
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriNorms
operator|.
name|length
argument_list|,
name|testNorms
operator|.
name|length
argument_list|)
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
name|aprioriNorms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"norms does not equals for field "
operator|+
name|field
operator|+
literal|" in document "
operator|+
name|i
argument_list|,
name|aprioriNorms
index|[
name|i
index|]
argument_list|,
name|testNorms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|docIndex
init|=
literal|0
init|;
name|docIndex
operator|<
name|aprioriReader
operator|.
name|numDocs
argument_list|()
condition|;
name|docIndex
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|aprioriReader
operator|.
name|isDeleted
argument_list|(
name|docIndex
argument_list|)
argument_list|,
name|testReader
operator|.
name|isDeleted
argument_list|(
name|docIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// compare term enumeration stepping
name|TermEnum
name|aprioriTermEnum
init|=
name|aprioriReader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|TermEnum
name|testTermEnum
init|=
name|testReader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|aprioriTermEnum
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|testTermEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|assertTrue
argument_list|(
name|testTermEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|,
name|testTermEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aprioriTermEnum
operator|.
name|docFreq
argument_list|()
operator|==
name|testTermEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
comment|// compare termDocs seeking
name|TermDocs
name|aprioriTermDocsSeeker
init|=
name|aprioriReader
operator|.
name|termDocs
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
name|TermDocs
name|testTermDocsSeeker
init|=
name|testReader
operator|.
name|termDocs
argument_list|(
name|testTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|aprioriTermDocsSeeker
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|testTermDocsSeeker
operator|.
name|skipTo
argument_list|(
name|aprioriTermDocsSeeker
operator|.
name|doc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocsSeeker
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocsSeeker
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|aprioriTermDocsSeeker
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermDocsSeeker
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// compare documents per term
name|assertEquals
argument_list|(
name|aprioriReader
operator|.
name|docFreq
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|,
name|testReader
operator|.
name|docFreq
argument_list|(
name|testTermEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TermDocs
name|aprioriTermDocs
init|=
name|aprioriReader
operator|.
name|termDocs
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
name|TermDocs
name|testTermDocs
init|=
name|testReader
operator|.
name|termDocs
argument_list|(
name|testTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|aprioriTermDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|testTermDocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|assertTrue
argument_list|(
name|testTermDocs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|doc
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermDocs
operator|.
name|freq
argument_list|()
argument_list|,
name|testTermDocs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|aprioriTermDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermDocs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// compare term positions
name|TermPositions
name|testTermPositions
init|=
name|testReader
operator|.
name|termPositions
argument_list|(
name|testTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
name|TermPositions
name|aprioriTermPositions
init|=
name|aprioriReader
operator|.
name|termPositions
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aprioriTermPositions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|docIndex
init|=
literal|0
init|;
name|docIndex
operator|<
name|aprioriReader
operator|.
name|maxDoc
argument_list|()
condition|;
name|docIndex
operator|++
control|)
block|{
name|boolean
name|hasNext
init|=
name|aprioriTermPositions
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasNext
condition|)
block|{
name|assertTrue
argument_list|(
name|testTermPositions
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermPositions
operator|.
name|freq
argument_list|()
argument_list|,
name|testTermPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|termPositionIndex
init|=
literal|0
init|;
name|termPositionIndex
operator|<
name|aprioriTermPositions
operator|.
name|freq
argument_list|()
condition|;
name|termPositionIndex
operator|++
control|)
block|{
name|int
name|aprioriPos
init|=
name|aprioriTermPositions
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|int
name|testPos
init|=
name|testTermPositions
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|aprioriPos
operator|!=
name|testPos
condition|)
block|{
name|assertEquals
argument_list|(
name|aprioriPos
argument_list|,
name|testPos
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|aprioriTermPositions
operator|.
name|isPayloadAvailable
argument_list|()
argument_list|,
name|testTermPositions
operator|.
name|isPayloadAvailable
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|aprioriTermPositions
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|aprioriTermPositions
operator|.
name|getPayloadLength
argument_list|()
argument_list|,
name|testTermPositions
operator|.
name|getPayloadLength
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|aprioriPayloads
init|=
name|aprioriTermPositions
operator|.
name|getPayload
argument_list|(
operator|new
name|byte
index|[
name|aprioriTermPositions
operator|.
name|getPayloadLength
argument_list|()
index|]
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|byte
index|[]
name|testPayloads
init|=
name|testTermPositions
operator|.
name|getPayload
argument_list|(
operator|new
name|byte
index|[
name|testTermPositions
operator|.
name|getPayloadLength
argument_list|()
index|]
argument_list|,
literal|0
argument_list|)
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
name|aprioriPayloads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|aprioriPayloads
index|[
name|i
index|]
argument_list|,
name|testPayloads
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|aprioriTermPositions
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermPositions
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// compare term enumeration seeking
name|aprioriTermEnum
operator|=
name|aprioriReader
operator|.
name|terms
argument_list|()
expr_stmt|;
name|TermEnum
name|aprioriTermEnumSeeker
init|=
name|aprioriReader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|TermEnum
name|testTermEnumSeeker
init|=
name|testReader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|aprioriTermEnum
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|aprioriTermEnumSeeker
operator|.
name|skipTo
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|testTermEnumSeeker
operator|.
name|skipTo
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermEnumSeeker
operator|.
name|term
argument_list|()
argument_list|,
name|testTermEnumSeeker
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|testTermEnumSeeker
operator|.
name|skipTo
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|aprioriTermEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|aprioriTermEnumSeeker
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermEnumSeeker
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// skip to non existing terms
name|aprioriTermEnumSeeker
operator|=
name|aprioriReader
operator|.
name|terms
argument_list|()
expr_stmt|;
name|testTermEnumSeeker
operator|=
name|testReader
operator|.
name|terms
argument_list|()
expr_stmt|;
name|aprioriTermEnum
operator|=
name|aprioriReader
operator|.
name|terms
argument_list|()
expr_stmt|;
name|aprioriTermEnum
operator|.
name|next
argument_list|()
expr_stmt|;
name|Term
name|nonExistingTerm
init|=
operator|new
name|Term
argument_list|(
name|aprioriTermEnum
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
literal|"bzzzzoo993djdj380sdf"
argument_list|)
decl_stmt|;
name|aprioriTermEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermEnumSeeker
operator|.
name|skipTo
argument_list|(
name|nonExistingTerm
argument_list|)
argument_list|,
name|testTermEnumSeeker
operator|.
name|skipTo
argument_list|(
name|nonExistingTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aprioriTermEnumSeeker
operator|.
name|term
argument_list|()
argument_list|,
name|testTermEnumSeeker
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|aprioriTermEnumSeeker
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermEnumSeeker
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// compare term vectors and position vectors
for|for
control|(
name|int
name|documentNumber
init|=
literal|0
init|;
name|documentNumber
operator|<
name|aprioriReader
operator|.
name|numDocs
argument_list|()
condition|;
name|documentNumber
operator|++
control|)
block|{
if|if
condition|(
name|documentNumber
operator|>
literal|0
condition|)
block|{
name|assertNotNull
argument_list|(
name|aprioriReader
operator|.
name|getTermFreqVector
argument_list|(
name|documentNumber
argument_list|,
literal|"b0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|aprioriReader
operator|.
name|getTermFreqVector
argument_list|(
name|documentNumber
argument_list|,
literal|"b1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testReader
operator|.
name|getTermFreqVector
argument_list|(
name|documentNumber
argument_list|,
literal|"b0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|testReader
operator|.
name|getTermFreqVector
argument_list|(
name|documentNumber
argument_list|,
literal|"b1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TermFreqVector
index|[]
name|aprioriFreqVectors
init|=
name|aprioriReader
operator|.
name|getTermFreqVectors
argument_list|(
name|documentNumber
argument_list|)
decl_stmt|;
name|TermFreqVector
index|[]
name|testFreqVectors
init|=
name|testReader
operator|.
name|getTermFreqVectors
argument_list|(
name|documentNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|aprioriFreqVectors
operator|!=
literal|null
operator|&&
name|testFreqVectors
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|aprioriFreqVectors
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TermFreqVector
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|TermFreqVector
name|termFreqVector
parameter_list|,
name|TermFreqVector
name|termFreqVector1
parameter_list|)
block|{
return|return
name|termFreqVector
operator|.
name|getField
argument_list|()
operator|.
name|compareTo
argument_list|(
name|termFreqVector1
operator|.
name|getField
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|testFreqVectors
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TermFreqVector
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|TermFreqVector
name|termFreqVector
parameter_list|,
name|TermFreqVector
name|termFreqVector1
parameter_list|)
block|{
return|return
name|termFreqVector
operator|.
name|getField
argument_list|()
operator|.
name|compareTo
argument_list|(
name|termFreqVector1
operator|.
name|getField
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"document "
operator|+
name|documentNumber
operator|+
literal|" vectors does not match"
argument_list|,
name|aprioriFreqVectors
operator|.
name|length
argument_list|,
name|testFreqVectors
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|freqVectorIndex
init|=
literal|0
init|;
name|freqVectorIndex
operator|<
name|aprioriFreqVectors
operator|.
name|length
condition|;
name|freqVectorIndex
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|aprioriFreqVectors
index|[
name|freqVectorIndex
index|]
operator|.
name|getTermFrequencies
argument_list|()
argument_list|,
name|testFreqVectors
index|[
name|freqVectorIndex
index|]
operator|.
name|getTermFrequencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|aprioriFreqVectors
index|[
name|freqVectorIndex
index|]
operator|.
name|getTerms
argument_list|()
argument_list|,
name|testFreqVectors
index|[
name|freqVectorIndex
index|]
operator|.
name|getTerms
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|aprioriFreqVectors
index|[
name|freqVectorIndex
index|]
operator|instanceof
name|TermPositionVector
condition|)
block|{
name|TermPositionVector
name|aprioriTermPositionVector
init|=
operator|(
name|TermPositionVector
operator|)
name|aprioriFreqVectors
index|[
name|freqVectorIndex
index|]
decl_stmt|;
name|TermPositionVector
name|testTermPositionVector
init|=
operator|(
name|TermPositionVector
operator|)
name|testFreqVectors
index|[
name|freqVectorIndex
index|]
decl_stmt|;
for|for
control|(
name|int
name|positionVectorIndex
init|=
literal|0
init|;
name|positionVectorIndex
operator|<
name|aprioriFreqVectors
index|[
name|freqVectorIndex
index|]
operator|.
name|getTerms
argument_list|()
operator|.
name|length
condition|;
name|positionVectorIndex
operator|++
control|)
block|{
if|if
condition|(
name|aprioriTermPositionVector
operator|.
name|getOffsets
argument_list|(
name|positionVectorIndex
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|aprioriTermPositionVector
operator|.
name|getOffsets
argument_list|(
name|positionVectorIndex
argument_list|)
argument_list|,
name|testTermPositionVector
operator|.
name|getOffsets
argument_list|(
name|positionVectorIndex
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aprioriTermPositionVector
operator|.
name|getTermPositions
argument_list|(
name|positionVectorIndex
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|aprioriTermPositionVector
operator|.
name|getTermPositions
argument_list|(
name|positionVectorIndex
argument_list|)
argument_list|,
name|testTermPositionVector
operator|.
name|getTermPositions
argument_list|(
name|positionVectorIndex
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
name|aprioriTermEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|testTermEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|aprioriReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|testReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
