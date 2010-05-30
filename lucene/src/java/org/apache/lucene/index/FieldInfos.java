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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Fieldable
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
name|IndexInput
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
name|IndexOutput
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
name|StringHelper
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
comment|/** Access to the Fieldable Info file that describes document fields and whether or  *  not they are indexed. Each segment has a separate Fieldable Info file. Objects  *  of this class are thread-safe for multiple readers, but only one thread can  *  be adding documents at a time, with no other reader or writer threads  *  accessing this object.  *  @lucene.experimental  */
end_comment
begin_class
DECL|class|FieldInfos
specifier|public
specifier|final
class|class
name|FieldInfos
block|{
comment|// First used in 2.9; prior to 2.9 there was no format header
DECL|field|FORMAT_START
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_START
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|CURRENT_FORMAT
specifier|static
specifier|final
name|int
name|CURRENT_FORMAT
init|=
name|FORMAT_START
decl_stmt|;
DECL|field|IS_INDEXED
specifier|static
specifier|final
name|byte
name|IS_INDEXED
init|=
literal|0x1
decl_stmt|;
DECL|field|STORE_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_TERMVECTOR
init|=
literal|0x2
decl_stmt|;
DECL|field|STORE_POSITIONS_WITH_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_POSITIONS_WITH_TERMVECTOR
init|=
literal|0x4
decl_stmt|;
DECL|field|STORE_OFFSET_WITH_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_OFFSET_WITH_TERMVECTOR
init|=
literal|0x8
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|static
specifier|final
name|byte
name|OMIT_NORMS
init|=
literal|0x10
decl_stmt|;
DECL|field|STORE_PAYLOADS
specifier|static
specifier|final
name|byte
name|STORE_PAYLOADS
init|=
literal|0x20
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|static
specifier|final
name|byte
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|0x40
decl_stmt|;
DECL|field|byNumber
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
name|byNumber
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|format
specifier|private
name|int
name|format
decl_stmt|;
DECL|method|FieldInfos
name|FieldInfos
parameter_list|()
block|{ }
comment|/**    * Construct a FieldInfos object using the directory and the name of the file    * IndexInput    * @param d The directory to open the IndexInput from    * @param name The name of the file to open the IndexInput from in the Directory    * @throws IOException    */
DECL|method|FieldInfos
name|FieldInfos
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|d
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|read
argument_list|(
name|input
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a deep clone of this FieldInfos instance.    */
annotation|@
name|Override
DECL|method|clone
specifier|synchronized
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|FieldInfos
name|fis
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numField
init|=
name|byNumber
operator|.
name|size
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
name|numField
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
call|(
name|FieldInfo
call|)
argument_list|(
name|byNumber
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|clone
argument_list|()
decl_stmt|;
name|fis
operator|.
name|byNumber
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|fis
operator|.
name|byName
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
block|}
return|return
name|fis
return|;
block|}
comment|/** Adds field info for a Document. */
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|List
argument_list|<
name|Fieldable
argument_list|>
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|fields
control|)
block|{
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|isIndexed
argument_list|()
argument_list|,
name|field
operator|.
name|isTermVectorStored
argument_list|()
argument_list|,
name|field
operator|.
name|isStorePositionWithTermVector
argument_list|()
argument_list|,
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
argument_list|,
name|field
operator|.
name|getOmitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|field
operator|.
name|getOmitTermFreqAndPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns true if any fields do not omitTermFreqAndPositions */
DECL|method|hasProx
specifier|public
name|boolean
name|hasProx
parameter_list|()
block|{
specifier|final
name|int
name|numFields
init|=
name|byNumber
operator|.
name|size
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
specifier|final
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|fi
operator|.
name|omitTermFreqAndPositions
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Add fields that are indexed. Whether they have termvectors has to be specified.    *     * @param names The names of the fields    * @param storeTermVectors Whether the fields store term vectors or not    * @param storePositionWithTermVector true if positions should be stored.    * @param storeOffsetWithTermVector true if offsets should be stored    */
DECL|method|addIndexed
specifier|synchronized
specifier|public
name|void
name|addIndexed
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|boolean
name|storeTermVectors
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|add
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|storeTermVectors
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assumes the fields are not storing term vectors.    *     * @param names The names of the fields    * @param isIndexed Whether the fields are indexed or not    *     * @see #add(String, boolean)    */
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Calls 5 parameter add with false for all TermVector parameters.    *     * @param name The name of the Fieldable    * @param isIndexed true if the field is indexed    * @see #add(String, boolean, boolean, boolean, boolean)    */
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls 5 parameter add with false for term vector positions and offsets.    *     * @param name The name of the field    * @param isIndexed  true if the field is indexed    * @param storeTermVector true if the term vector should be stored    */
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for the TermVector    * parameters.    *     * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    * @param storePositionWithTermVector true if the term vector with positions should be stored    * @param storeOffsetWithTermVector true if the term vector with offsets should be stored    */
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for the TermVector    * parameters.    *    * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    * @param storePositionWithTermVector true if the term vector with positions should be stored    * @param storeOffsetWithTermVector true if the term vector with offsets should be stored    * @param omitNorms true if the norms for the indexed field should be omitted    */
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for the TermVector    * parameters.    *    * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    * @param storePositionWithTermVector true if the term vector with positions should be stored    * @param storeOffsetWithTermVector true if the term vector with offsets should be stored    * @param omitNorms true if the norms for the indexed field should be omitted    * @param storePayloads true if payloads should be stored for this field    * @param omitTermFreqAndPositions true if term freqs should be omitted for this field    */
DECL|method|add
specifier|synchronized
specifier|public
name|FieldInfo
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
return|return
name|addInternal
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|omitTermFreqAndPositions
argument_list|)
return|;
block|}
else|else
block|{
name|fi
operator|.
name|update
argument_list|(
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|omitTermFreqAndPositions
argument_list|)
expr_stmt|;
block|}
return|return
name|fi
return|;
block|}
DECL|method|addInternal
specifier|private
name|FieldInfo
name|addInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
name|name
operator|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|FieldInfo
name|fi
init|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|byNumber
operator|.
name|size
argument_list|()
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|omitTermFreqAndPositions
argument_list|)
decl_stmt|;
name|byNumber
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|byName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
return|return
name|fi
return|;
block|}
DECL|method|fieldNumber
specifier|public
name|int
name|fieldNumber
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
operator|(
name|fi
operator|!=
literal|null
operator|)
condition|?
name|fi
operator|.
name|number
else|:
operator|-
literal|1
return|;
block|}
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/**    * Return the fieldName identified by its number.    *     * @param fieldNumber    * @return the fieldName or an empty string when the field    * with the given number doesn't exist.    */
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
return|return
operator|(
name|fi
operator|!=
literal|null
operator|)
condition|?
name|fi
operator|.
name|name
else|:
literal|""
return|;
block|}
comment|/**    * Return the fieldinfo object referenced by the fieldNumber.    * @param fieldNumber    * @return the FieldInfo object or null when the given fieldNumber    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|fieldNumber
operator|>=
literal|0
operator|)
condition|?
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
name|boolean
name|hasVectors
init|=
literal|false
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fieldInfo
argument_list|(
name|i
argument_list|)
operator|.
name|storeTermVector
condition|)
block|{
name|hasVectors
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|hasVectors
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|output
init|=
name|d
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|write
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|CURRENT_FORMAT
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|()
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
name|bits
operator||=
name|IS_INDEXED
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|storeTermVector
condition|)
name|bits
operator||=
name|STORE_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|storePositionWithTermVector
condition|)
name|bits
operator||=
name|STORE_POSITIONS_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|storeOffsetWithTermVector
condition|)
name|bits
operator||=
name|STORE_OFFSET_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|omitNorms
condition|)
name|bits
operator||=
name|OMIT_NORMS
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|storePayloads
condition|)
name|bits
operator||=
name|STORE_PAYLOADS
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|omitTermFreqAndPositions
condition|)
name|bits
operator||=
name|OMIT_TERM_FREQ_AND_POSITIONS
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|private
name|void
name|read
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|format
operator|=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|format
operator|>
name|FORMAT_START
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"unrecognized format "
operator|+
name|format
operator|+
literal|" in file \""
operator|+
name|fileName
operator|+
literal|"\""
argument_list|)
throw|;
block|}
specifier|final
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//read in the size
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|byte
name|bits
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isIndexed
init|=
operator|(
name|bits
operator|&
name|IS_INDEXED
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storeTermVector
init|=
operator|(
name|bits
operator|&
name|STORE_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storePositionsWithTermVector
init|=
operator|(
name|bits
operator|&
name|STORE_POSITIONS_WITH_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storeOffsetWithTermVector
init|=
operator|(
name|bits
operator|&
name|STORE_OFFSET_WITH_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|omitNorms
init|=
operator|(
name|bits
operator|&
name|OMIT_NORMS
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storePayloads
init|=
operator|(
name|bits
operator|&
name|STORE_PAYLOADS
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|omitTermFreqAndPositions
init|=
operator|(
name|bits
operator|&
name|OMIT_TERM_FREQ_AND_POSITIONS
operator|)
operator|!=
literal|0
decl_stmt|;
name|addInternal
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|storePositionsWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|omitTermFreqAndPositions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|!=
name|input
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"did not read all bytes from file \""
operator|+
name|fileName
operator|+
literal|"\": read "
operator|+
name|input
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" vs size "
operator|+
name|input
operator|.
name|length
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
