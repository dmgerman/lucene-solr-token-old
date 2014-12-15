begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|TermVectorsFormat
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
name|TermVectorsReader
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
name|TermVectorsWriter
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
name|AssertingLeafReader
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
name|Fields
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
name|SegmentInfo
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
name|IOContext
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Just like the default vectors format but with additional asserts.  */
end_comment
begin_class
DECL|class|AssertingTermVectorsFormat
specifier|public
class|class
name|AssertingTermVectorsFormat
extends|extends
name|TermVectorsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|TermVectorsFormat
name|in
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
operator|.
name|termVectorsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|vectorsReader
specifier|public
name|TermVectorsReader
name|vectorsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingTermVectorsReader
argument_list|(
name|in
operator|.
name|vectorsReader
argument_list|(
name|directory
argument_list|,
name|segmentInfo
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|vectorsWriter
specifier|public
name|TermVectorsWriter
name|vectorsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingTermVectorsWriter
argument_list|(
name|in
operator|.
name|vectorsWriter
argument_list|(
name|directory
argument_list|,
name|segmentInfo
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AssertingTermVectorsReader
specifier|static
class|class
name|AssertingTermVectorsReader
extends|extends
name|TermVectorsReader
block|{
DECL|field|in
specifier|private
specifier|final
name|TermVectorsReader
name|in
decl_stmt|;
DECL|method|AssertingTermVectorsReader
name|AssertingTermVectorsReader
parameter_list|(
name|TermVectorsReader
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
comment|// do a few simple checks on init
assert|assert
name|toString
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|ramBytesUsed
argument_list|()
operator|>=
literal|0
assert|;
assert|assert
name|getChildResources
argument_list|()
operator|!=
literal|null
assert|;
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Fields
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|in
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|AssertingLeafReader
operator|.
name|AssertingFields
argument_list|(
name|fields
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|TermVectorsReader
name|clone
parameter_list|()
block|{
return|return
operator|new
name|AssertingTermVectorsReader
argument_list|(
name|in
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|v
init|=
name|in
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
assert|assert
name|v
operator|>=
literal|0
assert|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|Iterable
argument_list|<
name|Accountable
argument_list|>
name|res
init|=
name|in
operator|.
name|getChildResources
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|checkIterator
argument_list|(
name|res
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
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
name|in
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|TermVectorsReader
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingTermVectorsReader
argument_list|(
name|in
operator|.
name|getMergeInstance
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
DECL|enum|Status
enum|enum
name|Status
block|{
DECL|enum constant|UNDEFINED
DECL|enum constant|STARTED
DECL|enum constant|FINISHED
name|UNDEFINED
block|,
name|STARTED
block|,
name|FINISHED
block|;   }
DECL|class|AssertingTermVectorsWriter
specifier|static
class|class
name|AssertingTermVectorsWriter
extends|extends
name|TermVectorsWriter
block|{
DECL|field|in
specifier|private
specifier|final
name|TermVectorsWriter
name|in
decl_stmt|;
DECL|field|docStatus
DECL|field|fieldStatus
DECL|field|termStatus
specifier|private
name|Status
name|docStatus
decl_stmt|,
name|fieldStatus
decl_stmt|,
name|termStatus
decl_stmt|;
DECL|field|docCount
DECL|field|fieldCount
DECL|field|termCount
DECL|field|positionCount
specifier|private
name|int
name|docCount
decl_stmt|,
name|fieldCount
decl_stmt|,
name|termCount
decl_stmt|,
name|positionCount
decl_stmt|;
DECL|field|hasPositions
name|boolean
name|hasPositions
decl_stmt|;
DECL|method|AssertingTermVectorsWriter
name|AssertingTermVectorsWriter
parameter_list|(
name|TermVectorsWriter
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|docStatus
operator|=
name|Status
operator|.
name|UNDEFINED
expr_stmt|;
name|fieldStatus
operator|=
name|Status
operator|.
name|UNDEFINED
expr_stmt|;
name|termStatus
operator|=
name|Status
operator|.
name|UNDEFINED
expr_stmt|;
name|fieldCount
operator|=
name|termCount
operator|=
name|positionCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|(
name|int
name|numVectorFields
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fieldCount
operator|==
literal|0
assert|;
assert|assert
name|docStatus
operator|!=
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|startDocument
argument_list|(
name|numVectorFields
argument_list|)
expr_stmt|;
name|docStatus
operator|=
name|Status
operator|.
name|STARTED
expr_stmt|;
name|fieldCount
operator|=
name|numVectorFields
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
specifier|public
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|fieldCount
operator|==
literal|0
assert|;
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
name|docStatus
operator|=
name|Status
operator|.
name|FINISHED
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startField
specifier|public
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|positions
parameter_list|,
name|boolean
name|offsets
parameter_list|,
name|boolean
name|payloads
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termCount
operator|==
literal|0
assert|;
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|fieldStatus
operator|!=
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|startField
argument_list|(
name|info
argument_list|,
name|numTerms
argument_list|,
name|positions
argument_list|,
name|offsets
argument_list|,
name|payloads
argument_list|)
expr_stmt|;
name|fieldStatus
operator|=
name|Status
operator|.
name|STARTED
expr_stmt|;
name|termCount
operator|=
name|numTerms
expr_stmt|;
name|hasPositions
operator|=
name|positions
operator|||
name|offsets
operator|||
name|payloads
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishField
specifier|public
name|void
name|finishField
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|termCount
operator|==
literal|0
assert|;
assert|assert
name|fieldStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|finishField
argument_list|()
expr_stmt|;
name|fieldStatus
operator|=
name|Status
operator|.
name|FINISHED
expr_stmt|;
operator|--
name|fieldCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|fieldStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|termStatus
operator|!=
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|startTerm
argument_list|(
name|term
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|termStatus
operator|=
name|Status
operator|.
name|STARTED
expr_stmt|;
name|positionCount
operator|=
name|hasPositions
condition|?
name|freq
else|:
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|positionCount
operator|==
literal|0
assert|;
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|fieldStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|termStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|finishTerm
argument_list|()
expr_stmt|;
name|termStatus
operator|=
name|Status
operator|.
name|FINISHED
expr_stmt|;
operator|--
name|termCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|fieldStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|termStatus
operator|==
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|,
name|payload
argument_list|)
expr_stmt|;
operator|--
name|positionCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docCount
operator|==
name|numDocs
assert|;
assert|assert
name|docStatus
operator|==
operator|(
name|numDocs
operator|>
literal|0
condition|?
name|Status
operator|.
name|FINISHED
else|:
name|Status
operator|.
name|UNDEFINED
operator|)
assert|;
assert|assert
name|fieldStatus
operator|!=
name|Status
operator|.
name|STARTED
assert|;
assert|assert
name|termStatus
operator|!=
name|Status
operator|.
name|STARTED
assert|;
name|in
operator|.
name|finish
argument_list|(
name|fis
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
