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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|codecs
operator|.
name|FieldsProducer
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
name|index
operator|.
name|CodecReader
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
name|DirectoryReader
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
name|FieldInfo
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
name|FieldInfos
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
name|FilterCodecReader
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
name|FilteredTermsEnum
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
name|LeafReaderContext
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
name|Terms
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
name|TermsEnum
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Accountable
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
begin_class
DECL|class|TestMultiTermsEnum
specifier|public
class|class
name|TestMultiTermsEnum
extends|extends
name|LuceneTestCase
block|{
comment|// LUCENE-6826
DECL|method|testNoTermsInField
specifier|public
name|void
name|testNoTermsInField
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"deleted"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|directory2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory2
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|CodecReader
index|[]
name|codecReaders
init|=
operator|new
name|CodecReader
index|[
name|leaves
operator|.
name|size
argument_list|()
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
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|codecReaders
index|[
name|i
index|]
operator|=
operator|new
name|MigratingCodecReader
argument_list|(
operator|(
name|CodecReader
operator|)
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addIndexes
argument_list|(
name|codecReaders
argument_list|)
expr_stmt|;
comment|//<- bang
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|reader
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
DECL|class|MigratingCodecReader
specifier|private
specifier|static
class|class
name|MigratingCodecReader
extends|extends
name|FilterCodecReader
block|{
DECL|method|MigratingCodecReader
name|MigratingCodecReader
parameter_list|(
name|CodecReader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPostingsReader
specifier|public
name|FieldsProducer
name|getPostingsReader
parameter_list|()
block|{
return|return
operator|new
name|MigratingFieldsProducer
argument_list|(
name|super
operator|.
name|getPostingsReader
argument_list|()
argument_list|,
name|getFieldInfos
argument_list|()
argument_list|)
return|;
block|}
DECL|class|MigratingFieldsProducer
specifier|private
specifier|static
class|class
name|MigratingFieldsProducer
extends|extends
name|BaseMigratingFieldsProducer
block|{
DECL|method|MigratingFieldsProducer
name|MigratingFieldsProducer
parameter_list|(
name|FieldsProducer
name|delegate
parameter_list|,
name|FieldInfos
name|newFieldInfo
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
name|newFieldInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"deleted"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|Terms
name|deletedTerms
init|=
name|super
operator|.
name|terms
argument_list|(
literal|"deleted"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletedTerms
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ValueFilteredTerms
argument_list|(
name|deletedTerms
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"1"
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|FieldsProducer
name|create
parameter_list|(
name|FieldsProducer
name|delegate
parameter_list|,
name|FieldInfos
name|newFieldInfo
parameter_list|)
block|{
return|return
operator|new
name|MigratingFieldsProducer
argument_list|(
name|delegate
argument_list|,
name|newFieldInfo
argument_list|)
return|;
block|}
DECL|class|ValueFilteredTerms
specifier|private
specifier|static
class|class
name|ValueFilteredTerms
extends|extends
name|Terms
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Terms
name|delegate
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|BytesRef
name|value
decl_stmt|;
DECL|method|ValueFilteredTerms
specifier|public
name|ValueFilteredTerms
parameter_list|(
name|Terms
name|delegate
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilteredTermsEnum
argument_list|(
name|delegate
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|int
name|comparison
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparison
operator|<
literal|0
condition|)
block|{
comment|// I don't think it will actually get here because they are supposed to call nextSeekTerm
comment|// to get the initial term to seek to.
return|return
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
block|}
elseif|else
if|if
condition|(
name|comparison
operator|>
literal|0
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
else|else
block|{
comment|// comparison == 0
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|currentTerm
parameter_list|)
block|{
if|if
condition|(
name|currentTerm
operator|==
literal|null
operator|||
name|currentTerm
operator|.
name|compareTo
argument_list|(
name|value
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|value
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Docs say we can return -1 if we don't know.
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Docs say we can return -1 if we don't know.
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Docs say we can return -1 if we don't know.
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Docs say we can return -1 if we don't know.
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|hasFreqs
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasFreqs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasOffsets
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasPositions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasPayloads
argument_list|()
return|;
block|}
block|}
block|}
DECL|class|BaseMigratingFieldsProducer
specifier|private
specifier|static
class|class
name|BaseMigratingFieldsProducer
extends|extends
name|FieldsProducer
block|{
DECL|field|delegate
specifier|private
specifier|final
name|FieldsProducer
name|delegate
decl_stmt|;
DECL|field|newFieldInfo
specifier|private
specifier|final
name|FieldInfos
name|newFieldInfo
decl_stmt|;
DECL|method|BaseMigratingFieldsProducer
specifier|public
name|BaseMigratingFieldsProducer
parameter_list|(
name|FieldsProducer
name|delegate
parameter_list|,
name|FieldInfos
name|newFieldInfo
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|newFieldInfo
operator|=
name|newFieldInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|FieldInfo
argument_list|>
name|fieldInfoIterator
init|=
name|newFieldInfo
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|fieldInfoIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
name|fieldInfoIterator
operator|.
name|next
argument_list|()
operator|.
name|name
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|newFieldInfo
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|FieldsProducer
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|create
argument_list|(
name|delegate
operator|.
name|getMergeInstance
argument_list|()
argument_list|,
name|newFieldInfo
argument_list|)
return|;
block|}
DECL|method|create
specifier|protected
name|FieldsProducer
name|create
parameter_list|(
name|FieldsProducer
name|delegate
parameter_list|,
name|FieldInfos
name|newFieldInfo
parameter_list|)
block|{
return|return
operator|new
name|BaseMigratingFieldsProducer
argument_list|(
name|delegate
argument_list|,
name|newFieldInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getChildResources
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
