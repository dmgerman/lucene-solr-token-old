begin_unit
begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
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
name|index
operator|.
name|AtomicReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|StorableField
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
name|IndexSearcher
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
name|MatchAllDocsQuery
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
name|ScoreDoc
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
name|TopDocs
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
name|Version
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
begin_comment
comment|/**  * Utility class for creating training / test / cross validation indexes from the original index.  */
end_comment
begin_class
DECL|class|DatasetSplitter
specifier|public
class|class
name|DatasetSplitter
block|{
DECL|field|crossValidationRatio
specifier|private
name|double
name|crossValidationRatio
decl_stmt|;
DECL|field|testRatio
specifier|private
name|double
name|testRatio
decl_stmt|;
DECL|method|DatasetSplitter
specifier|public
name|DatasetSplitter
parameter_list|(
name|double
name|testRatio
parameter_list|,
name|double
name|crossValidationRatio
parameter_list|)
block|{
name|this
operator|.
name|crossValidationRatio
operator|=
name|crossValidationRatio
expr_stmt|;
name|this
operator|.
name|testRatio
operator|=
name|testRatio
expr_stmt|;
block|}
DECL|method|split
specifier|public
name|void
name|split
parameter_list|(
name|AtomicReader
name|originalIndex
parameter_list|,
name|Directory
name|trainingIndex
parameter_list|,
name|Directory
name|testIndex
parameter_list|,
name|Directory
name|crossValidationIndex
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
modifier|...
name|fieldNames
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO : check that the passed fields are stored in the original index
comment|// create IWs for train / test / cv IDXs
name|IndexWriter
name|testWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|testIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|cvWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|crossValidationIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|trainingWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|trainingIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|size
init|=
name|originalIndex
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|originalIndex
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|// set the type to be indexed, stored, with term vectors
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|b
init|=
literal|0
decl_stmt|;
comment|// iterate over existing documents
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
comment|// create a new document for indexing
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldNames
operator|!=
literal|null
operator|&&
name|fieldNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldName
argument_list|,
name|originalIndex
operator|.
name|document
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|StorableField
name|storableField
range|:
name|originalIndex
operator|.
name|document
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|storableField
operator|.
name|readerValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|storableField
operator|.
name|name
argument_list|()
argument_list|,
name|storableField
operator|.
name|readerValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|storableField
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|storableField
operator|.
name|name
argument_list|()
argument_list|,
name|storableField
operator|.
name|binaryValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|storableField
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|storableField
operator|.
name|name
argument_list|()
argument_list|,
name|storableField
operator|.
name|stringValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|storableField
operator|.
name|numericValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|storableField
operator|.
name|name
argument_list|()
argument_list|,
name|storableField
operator|.
name|numericValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// add it to one of the IDXs
if|if
condition|(
name|b
operator|%
literal|2
operator|==
literal|0
operator|&&
name|testWriter
operator|.
name|maxDoc
argument_list|()
operator|<
name|size
operator|*
name|testRatio
condition|)
block|{
name|testWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|testWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cvWriter
operator|.
name|maxDoc
argument_list|()
operator|<
name|size
operator|*
name|crossValidationRatio
condition|)
block|{
name|cvWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cvWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|trainingWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|trainingWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|b
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// close IWs
name|testWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|cvWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|trainingWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
