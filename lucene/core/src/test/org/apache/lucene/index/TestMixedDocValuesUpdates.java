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
name|HashSet
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
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
operator|.
name|Store
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
name|StringField
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
name|Bits
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
name|LuceneTestCase
operator|.
name|Nightly
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestMixedDocValuesUpdates
specifier|public
class|class
name|TestMixedDocValuesUpdates
extends|extends
name|LuceneTestCase
block|{
DECL|method|testManyReopensAndFields
specifier|public
name|void
name|testManyReopensAndFields
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
specifier|final
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
name|newLogMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// merge often
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isNRT
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|DirectoryReader
name|reader
decl_stmt|;
if|if
condition|(
name|isNRT
condition|)
block|{
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numFields
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|3
decl_stmt|;
comment|// 3-7
specifier|final
name|int
name|numNDVFields
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numFields
operator|/
literal|2
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// 1-3
specifier|final
name|long
index|[]
name|fieldValues
init|=
operator|new
name|long
index|[
name|numFields
index|]
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
name|fieldValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fieldValues
index|[
name|i
index|]
operator|=
literal|1
expr_stmt|;
block|}
name|int
name|numRounds
init|=
name|atLeast
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|int
name|docID
init|=
literal|0
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
name|numRounds
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|// System.out.println("TEST: round=" + i + ", numDocs=" + numDocs);
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
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
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc-"
operator|+
name|docID
argument_list|,
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
name|StringField
argument_list|(
literal|"key"
argument_list|,
literal|"all"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// update key
comment|// add all fields with their current value
for|for
control|(
name|int
name|f
init|=
literal|0
init|;
name|f
operator|<
name|fieldValues
operator|.
name|length
condition|;
name|f
operator|++
control|)
block|{
if|if
condition|(
name|f
operator|<
name|numNDVFields
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"f"
operator|+
name|f
argument_list|,
name|fieldValues
index|[
name|f
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
operator|+
name|f
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|fieldValues
index|[
name|f
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
operator|++
name|docID
expr_stmt|;
block|}
name|int
name|fieldIdx
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|fieldValues
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
name|updateField
init|=
literal|"f"
operator|+
name|fieldIdx
decl_stmt|;
if|if
condition|(
name|fieldIdx
operator|<
name|numNDVFields
condition|)
block|{
name|writer
operator|.
name|updateNumericDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"key"
argument_list|,
literal|"all"
argument_list|)
argument_list|,
name|updateField
argument_list|,
operator|++
name|fieldValues
index|[
name|fieldIdx
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|updateBinaryDocValue
argument_list|(
operator|new
name|Term
argument_list|(
literal|"key"
argument_list|,
literal|"all"
argument_list|)
argument_list|,
name|updateField
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
operator|++
name|fieldValues
index|[
name|fieldIdx
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("TEST: updated field '" + updateField + "' to value " + fieldValues[fieldIdx]);
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.2
condition|)
block|{
name|int
name|deleteDoc
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// might also delete an already deleted document, ok!
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc-"
operator|+
name|deleteDoc
argument_list|)
argument_list|)
expr_stmt|;
comment|//        System.out.println("[" + Thread.currentThread().getName() + "]: deleted document: doc-" + deleteDoc);
block|}
comment|// verify reader
if|if
condition|(
operator|!
name|isNRT
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|//      System.out.println("[" + Thread.currentThread().getName() + "]: reopen reader: " + reader);
name|DirectoryReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newReader
expr_stmt|;
comment|//      System.out.println("[" + Thread.currentThread().getName() + "]: reopened reader: " + reader);
name|assertTrue
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// we delete at most one document per round
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|//        System.out.println(((SegmentReader) r).getSegmentName());
name|Bits
name|liveDocs
init|=
name|r
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|field
init|=
literal|0
init|;
name|field
operator|<
name|fieldValues
operator|.
name|length
condition|;
name|field
operator|++
control|)
block|{
name|String
name|f
init|=
literal|"f"
operator|+
name|field
decl_stmt|;
name|BinaryDocValues
name|bdv
init|=
name|r
operator|.
name|getBinaryDocValues
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|NumericDocValues
name|ndv
init|=
name|r
operator|.
name|getNumericDocValues
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Bits
name|docsWithField
init|=
name|r
operator|.
name|getDocsWithField
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|<
name|numNDVFields
condition|)
block|{
name|assertNotNull
argument_list|(
name|ndv
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|bdv
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|ndv
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|bdv
argument_list|)
expr_stmt|;
block|}
name|int
name|maxDoc
init|=
name|r
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|//              System.out.println("doc=" + (doc + context.docBase) + " f='" + f + "' vslue=" + getValue(bdv, doc, scratch));
name|assertTrue
argument_list|(
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|<
name|numNDVFields
condition|)
block|{
name|assertEquals
argument_list|(
literal|"invalid numeric value for doc="
operator|+
name|doc
operator|+
literal|", field="
operator|+
name|f
operator|+
literal|", reader="
operator|+
name|r
argument_list|,
name|fieldValues
index|[
name|field
index|]
argument_list|,
name|ndv
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"invalid binary value for doc="
operator|+
name|doc
operator|+
literal|", field="
operator|+
name|f
operator|+
literal|", reader="
operator|+
name|r
argument_list|,
name|fieldValues
index|[
name|field
index|]
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|getValue
argument_list|(
name|bdv
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|//      System.out.println();
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testStressMultiThreading
specifier|public
name|void
name|testStressMultiThreading
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// create index
specifier|final
name|int
name|numFields
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|6
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|2000
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
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
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|group
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|String
name|g
decl_stmt|;
if|if
condition|(
name|group
operator|<
literal|0.1
condition|)
name|g
operator|=
literal|"g0"
expr_stmt|;
elseif|else
if|if
condition|(
name|group
operator|<
literal|0.5
condition|)
name|g
operator|=
literal|"g1"
expr_stmt|;
elseif|else
if|if
condition|(
name|group
operator|<
literal|0.8
condition|)
name|g
operator|=
literal|"g2"
expr_stmt|;
else|else
name|g
operator|=
literal|"g3"
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"updKey"
argument_list|,
name|g
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numFields
condition|;
name|j
operator|++
control|)
block|{
name|long
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
operator|+
name|j
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|value
argument_list|)
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
literal|"cf"
operator|+
name|j
argument_list|,
name|value
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// control, always updated to f * 2
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDownLatch
name|done
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numThreads
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|numUpdates
init|=
operator|new
name|AtomicInteger
argument_list|(
name|atLeast
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
comment|// same thread updates a field as well as reopens
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
literal|"UpdateThread-"
operator|+
name|i
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|DirectoryReader
name|reader
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
while|while
condition|(
name|numUpdates
operator|.
name|getAndDecrement
argument_list|()
operator|>
literal|0
condition|)
block|{
name|double
name|group
init|=
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|Term
name|t
decl_stmt|;
if|if
condition|(
name|group
operator|<
literal|0.1
condition|)
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"updKey"
argument_list|,
literal|"g0"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|group
operator|<
literal|0.5
condition|)
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"updKey"
argument_list|,
literal|"g1"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|group
operator|<
literal|0.8
condition|)
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"updKey"
argument_list|,
literal|"g2"
argument_list|)
expr_stmt|;
else|else
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"updKey"
argument_list|,
literal|"g3"
argument_list|)
expr_stmt|;
comment|//              System.out.println("[" + Thread.currentThread().getName() + "] numUpdates=" + numUpdates + " updateTerm=" + t);
name|int
name|field
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numFields
argument_list|)
decl_stmt|;
specifier|final
name|String
name|f
init|=
literal|"f"
operator|+
name|field
decl_stmt|;
specifier|final
name|String
name|cf
init|=
literal|"cf"
operator|+
name|field
decl_stmt|;
name|long
name|updValue
init|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
comment|//              System.err.println("[" + Thread.currentThread().getName() + "] t=" + t + ", f=" + f + ", updValue=" + updValue);
name|writer
operator|.
name|updateDocValues
argument_list|(
name|t
argument_list|,
operator|new
name|BinaryDocValuesField
argument_list|(
name|f
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|updValue
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NumericDocValuesField
argument_list|(
name|cf
argument_list|,
name|updValue
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.2
condition|)
block|{
comment|// delete a random document
name|int
name|doc
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
comment|//                System.out.println("[" + Thread.currentThread().getName() + "] deleteDoc=doc" + doc);
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.05
condition|)
block|{
comment|// commit every 20 updates on average
comment|//                  System.out.println("[" + Thread.currentThread().getName() + "] commit");
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.1
condition|)
block|{
comment|// reopen NRT reader (apply updates), on average once every 10 updates
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
comment|//                  System.out.println("[" + Thread.currentThread().getName() + "] open NRT");
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//                  System.out.println("[" + Thread.currentThread().getName() + "] reopen NRT");
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
decl_stmt|;
if|if
condition|(
name|r2
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|r2
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//            System.out.println("[" + Thread.currentThread().getName() + "] DONE");
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|success
condition|)
block|{
comment|// suppress this exception only if there was another exception
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
name|done
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|done
operator|.
name|await
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|r
init|=
name|context
operator|.
name|reader
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|BinaryDocValues
name|bdv
init|=
name|r
operator|.
name|getBinaryDocValues
argument_list|(
literal|"f"
operator|+
name|i
argument_list|)
decl_stmt|;
name|NumericDocValues
name|control
init|=
name|r
operator|.
name|getNumericDocValues
argument_list|(
literal|"cf"
operator|+
name|i
argument_list|)
decl_stmt|;
name|Bits
name|docsWithBdv
init|=
name|r
operator|.
name|getDocsWithField
argument_list|(
literal|"f"
operator|+
name|i
argument_list|)
decl_stmt|;
name|Bits
name|docsWithControl
init|=
name|r
operator|.
name|getDocsWithField
argument_list|(
literal|"cf"
operator|+
name|i
argument_list|)
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|r
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|docsWithBdv
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|docsWithControl
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|ctrlValue
init|=
name|control
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|long
name|bdvValue
init|=
name|TestBinaryDocValuesUpdates
operator|.
name|getValue
argument_list|(
name|bdv
argument_list|,
name|j
argument_list|)
operator|*
literal|2
decl_stmt|;
comment|//              if (ctrlValue != bdvValue) {
comment|//                System.out.println("seg=" + r + ", f=f" + i + ", doc=" + j + ", group=" + r.document(j).get("updKey") + ", ctrlValue=" + ctrlValue + ", bdvBytes=" + scratch);
comment|//              }
name|assertEquals
argument_list|(
name|ctrlValue
argument_list|,
name|bdvValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|reader
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
DECL|method|testUpdateDifferentDocsInDifferentGens
specifier|public
name|void
name|testUpdateDifferentDocsInDifferentGens
parameter_list|()
throws|throws
name|Exception
block|{
comment|// update same document multiple times across generations
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
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
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|value
argument_list|)
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
literal|"cf"
argument_list|,
name|value
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|int
name|numGens
init|=
name|atLeast
argument_list|(
literal|5
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
name|numGens
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|Term
name|t
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|doc
argument_list|)
decl_stmt|;
name|long
name|value
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|writer
operator|.
name|updateDocValues
argument_list|(
name|t
argument_list|,
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NumericDocValuesField
argument_list|(
literal|"cf"
argument_list|,
name|value
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|LeafReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|BinaryDocValues
name|fbdv
init|=
name|r
operator|.
name|getBinaryDocValues
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
name|NumericDocValues
name|cfndv
init|=
name|r
operator|.
name|getNumericDocValues
argument_list|(
literal|"cf"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|cfndv
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|getValue
argument_list|(
name|fbdv
argument_list|,
name|j
argument_list|)
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
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
name|Nightly
DECL|method|testTonsOfUpdates
specifier|public
name|void
name|testTonsOfUpdates
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-5248: make sure that when there are many updates, we don't use too much RAM
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
comment|// don't flush by doc
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// test data: lots of documents (few 10Ks) and lots of update terms (few hundreds)
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|20000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numBinaryFields
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
comment|// terms should affect many docs
name|Set
argument_list|<
name|String
argument_list|>
name|updateTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|updateTerms
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|updateTerms
operator|.
name|add
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//    System.out.println("numDocs=" + numDocs + " numBinaryFields=" + numBinaryFields + " numTerms=" + numTerms);
comment|// build a large index with many BDV fields and update terms
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|numUpdateTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|numTerms
operator|/
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numUpdateTerms
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"upd"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|updateTerms
argument_list|)
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numBinaryFields
condition|;
name|j
operator|++
control|)
block|{
name|long
name|val
init|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
operator|+
name|j
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|val
argument_list|)
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
literal|"cf"
operator|+
name|j
argument_list|,
name|val
operator|*
literal|2
argument_list|)
argument_list|)
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// commit so there's something to apply to
comment|// set to flush every 2048 bytes (approximately every 12 updates), so we get
comment|// many flushes during binary updates
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|2048.0
operator|/
literal|1024
operator|/
literal|1024
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numUpdates
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|//    System.out.println("numUpdates=" + numUpdates);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numUpdates
condition|;
name|i
operator|++
control|)
block|{
name|int
name|field
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numBinaryFields
argument_list|)
decl_stmt|;
name|Term
name|updateTerm
init|=
operator|new
name|Term
argument_list|(
literal|"upd"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|updateTerms
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|value
init|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|writer
operator|.
name|updateDocValues
argument_list|(
name|updateTerm
argument_list|,
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
operator|+
name|field
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|toBytes
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
operator|new
name|NumericDocValuesField
argument_list|(
literal|"cf"
operator|+
name|field
argument_list|,
name|value
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
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
name|numBinaryFields
condition|;
name|i
operator|++
control|)
block|{
name|LeafReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|BinaryDocValues
name|f
init|=
name|r
operator|.
name|getBinaryDocValues
argument_list|(
literal|"f"
operator|+
name|i
argument_list|)
decl_stmt|;
name|NumericDocValues
name|cf
init|=
name|r
operator|.
name|getNumericDocValues
argument_list|(
literal|"cf"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"reader="
operator|+
name|r
operator|+
literal|", field=f"
operator|+
name|i
operator|+
literal|", doc="
operator|+
name|j
argument_list|,
name|cf
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|TestBinaryDocValuesUpdates
operator|.
name|getValue
argument_list|(
name|f
argument_list|,
name|j
argument_list|)
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|reader
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
