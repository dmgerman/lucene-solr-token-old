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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Vector
import|;
end_import
begin_comment
comment|/**  * Writer works by opening a document and then opening the fields within the document and then  * writing out the vectors for each field.  *   * Rough usage:  *<CODE>  for each document  {  writer.openDocument();  for each field on the document  {  writer.openField(field);  for all of the terms  {  writer.addTerm(...)  }  writer.closeField  }  writer.closeDocument()      }</CODE>  *  * @version $Id$  *   */
end_comment
begin_class
DECL|class|TermVectorsWriter
specifier|final
class|class
name|TermVectorsWriter
block|{
DECL|field|STORE_POSITIONS_WITH_TERMVECTOR
specifier|public
specifier|static
specifier|final
name|byte
name|STORE_POSITIONS_WITH_TERMVECTOR
init|=
literal|0x1
decl_stmt|;
DECL|field|STORE_OFFSET_WITH_TERMVECTOR
specifier|public
specifier|static
specifier|final
name|byte
name|STORE_OFFSET_WITH_TERMVECTOR
init|=
literal|0x2
decl_stmt|;
DECL|field|FORMAT_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_VERSION
init|=
literal|2
decl_stmt|;
comment|//The size in bytes that the FORMAT_VERSION will take up at the beginning of each file
DECL|field|FORMAT_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|TVX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TVX_EXTENSION
init|=
literal|".tvx"
decl_stmt|;
DECL|field|TVD_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TVD_EXTENSION
init|=
literal|".tvd"
decl_stmt|;
DECL|field|TVF_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TVF_EXTENSION
init|=
literal|".tvf"
decl_stmt|;
DECL|field|tvx
DECL|field|tvd
DECL|field|tvf
specifier|private
name|IndexOutput
name|tvx
init|=
literal|null
decl_stmt|,
name|tvd
init|=
literal|null
decl_stmt|,
name|tvf
init|=
literal|null
decl_stmt|;
DECL|field|fields
specifier|private
name|Vector
name|fields
init|=
literal|null
decl_stmt|;
DECL|field|terms
specifier|private
name|Vector
name|terms
init|=
literal|null
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|currentField
specifier|private
name|TVField
name|currentField
init|=
literal|null
decl_stmt|;
DECL|field|currentDocPointer
specifier|private
name|long
name|currentDocPointer
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|TermVectorsWriter
specifier|public
name|TermVectorsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Open files for TermVector storage
name|tvx
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
name|TVX_EXTENSION
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeInt
argument_list|(
name|FORMAT_VERSION
argument_list|)
expr_stmt|;
name|tvd
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
name|TVD_EXTENSION
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeInt
argument_list|(
name|FORMAT_VERSION
argument_list|)
expr_stmt|;
name|tvf
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
name|TVF_EXTENSION
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeInt
argument_list|(
name|FORMAT_VERSION
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|fields
operator|=
operator|new
name|Vector
argument_list|(
name|fieldInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
block|}
DECL|method|openDocument
specifier|public
specifier|final
name|void
name|openDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|closeDocument
argument_list|()
expr_stmt|;
name|currentDocPointer
operator|=
name|tvd
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|closeDocument
specifier|public
specifier|final
name|void
name|closeDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isDocumentOpen
argument_list|()
condition|)
block|{
name|closeField
argument_list|()
expr_stmt|;
name|writeDoc
argument_list|()
expr_stmt|;
name|fields
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentDocPointer
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|isDocumentOpen
specifier|public
specifier|final
name|boolean
name|isDocumentOpen
parameter_list|()
block|{
return|return
name|currentDocPointer
operator|!=
operator|-
literal|1
return|;
block|}
comment|/** Start processing a field. This can be followed by a number of calls to    *  addTerm, and a final call to closeField to indicate the end of    *  processing of this field. If a field was previously open, it is    *  closed automatically.    */
DECL|method|openField
specifier|public
specifier|final
name|void
name|openField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|openField
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|fieldInfo
operator|.
name|storePositionWithTermVector
argument_list|,
name|fieldInfo
operator|.
name|storeOffsetWithTermVector
argument_list|)
expr_stmt|;
block|}
DECL|method|openField
specifier|private
name|void
name|openField
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isDocumentOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot open field when no document is open."
argument_list|)
throw|;
name|closeField
argument_list|()
expr_stmt|;
name|currentField
operator|=
operator|new
name|TVField
argument_list|(
name|fieldNumber
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
argument_list|)
expr_stmt|;
block|}
comment|/** Finished processing current field. This should be followed by a call to    *  openField before future calls to addTerm.    */
DECL|method|closeField
specifier|public
specifier|final
name|void
name|closeField
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isFieldOpen
argument_list|()
condition|)
block|{
comment|/* DEBUG */
comment|//System.out.println("closeField()");
comment|/* DEBUG */
comment|// save field and terms
name|writeField
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|currentField
argument_list|)
expr_stmt|;
name|terms
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentField
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Return true if a field is currently open. */
DECL|method|isFieldOpen
specifier|public
specifier|final
name|boolean
name|isFieldOpen
parameter_list|()
block|{
return|return
name|currentField
operator|!=
literal|null
return|;
block|}
comment|/** Add term to the field's term vector. Field must already be open    *  of NullPointerException is thrown. Terms should be added in    *  increasing order of terms, one call per unique termNum. ProxPointer    *  is a pointer into the TermPosition file (prx). Freq is the number of    *  times this term appears in this field, in this document.    */
DECL|method|addTerm
specifier|public
specifier|final
name|void
name|addTerm
parameter_list|(
name|String
name|termText
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|addTerm
argument_list|(
name|termText
argument_list|,
name|freq
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addTerm
specifier|public
specifier|final
name|void
name|addTerm
parameter_list|(
name|String
name|termText
parameter_list|,
name|int
name|freq
parameter_list|,
name|int
index|[]
name|positions
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isDocumentOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add terms when document is not open"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|isFieldOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add terms when field is not open"
argument_list|)
throw|;
name|addTermInternal
argument_list|(
name|termText
argument_list|,
name|freq
argument_list|,
name|positions
argument_list|,
name|offsets
argument_list|)
expr_stmt|;
block|}
DECL|method|addTermInternal
specifier|private
specifier|final
name|void
name|addTermInternal
parameter_list|(
name|String
name|termText
parameter_list|,
name|int
name|freq
parameter_list|,
name|int
index|[]
name|positions
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|)
block|{
name|TVTerm
name|term
init|=
operator|new
name|TVTerm
argument_list|()
decl_stmt|;
name|term
operator|.
name|termText
operator|=
name|termText
expr_stmt|;
name|term
operator|.
name|freq
operator|=
name|freq
expr_stmt|;
name|term
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
name|term
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a complete document specified by all its term vectors. If document has no    * term vectors, add value for tvx.    *     * @param vectors    * @throws IOException    */
DECL|method|addAllDocVectors
specifier|public
specifier|final
name|void
name|addAllDocVectors
parameter_list|(
name|TermFreqVector
index|[]
name|vectors
parameter_list|)
throws|throws
name|IOException
block|{
name|openDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|vectors
operator|!=
literal|null
condition|)
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
name|vectors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|storePositionWithTermVector
init|=
literal|false
decl_stmt|;
name|boolean
name|storeOffsetWithTermVector
init|=
literal|false
decl_stmt|;
try|try
block|{
name|TermPositionVector
name|tpVector
init|=
operator|(
name|TermPositionVector
operator|)
name|vectors
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|tpVector
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|tpVector
operator|.
name|getTermPositions
argument_list|(
literal|0
argument_list|)
operator|!=
literal|null
condition|)
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|tpVector
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|tpVector
operator|.
name|getOffsets
argument_list|(
literal|0
argument_list|)
operator|!=
literal|null
condition|)
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|tpVector
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|openField
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
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
name|tpVector
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
name|addTermInternal
argument_list|(
name|tpVector
operator|.
name|getTerms
argument_list|()
index|[
name|j
index|]
argument_list|,
name|tpVector
operator|.
name|getTermFrequencies
argument_list|()
index|[
name|j
index|]
argument_list|,
name|tpVector
operator|.
name|getTermPositions
argument_list|(
name|j
argument_list|)
argument_list|,
name|tpVector
operator|.
name|getOffsets
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|closeField
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|ignore
parameter_list|)
block|{
name|TermFreqVector
name|tfVector
init|=
name|vectors
index|[
name|i
index|]
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|tfVector
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|openField
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|storePositionWithTermVector
argument_list|,
name|storeOffsetWithTermVector
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
name|tfVector
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
name|addTermInternal
argument_list|(
name|tfVector
operator|.
name|getTerms
argument_list|()
index|[
name|j
index|]
argument_list|,
name|tfVector
operator|.
name|getTermFrequencies
argument_list|()
index|[
name|j
index|]
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|closeField
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|closeDocument
argument_list|()
expr_stmt|;
block|}
comment|/** Close all streams. */
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|closeDocument
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// make an effort to close all streams we can but remember and re-throw
comment|// the first exception encountered in this process
name|IOException
name|keep
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
try|try
block|{
name|tvx
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
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tvd
operator|!=
literal|null
condition|)
try|try
block|{
name|tvd
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
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tvf
operator|!=
literal|null
condition|)
try|try
block|{
name|tvf
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
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|keep
operator|!=
literal|null
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|keep
operator|.
name|fillInStackTrace
argument_list|()
throw|;
block|}
block|}
DECL|method|writeField
specifier|private
name|void
name|writeField
parameter_list|()
throws|throws
name|IOException
block|{
comment|// remember where this field is written
name|currentField
operator|.
name|tvfPointer
operator|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|//System.out.println("Field Pointer: " + currentField.tvfPointer);
specifier|final
name|int
name|size
init|=
name|terms
operator|.
name|size
argument_list|()
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|boolean
name|storePositions
init|=
name|currentField
operator|.
name|storePositions
decl_stmt|;
name|boolean
name|storeOffsets
init|=
name|currentField
operator|.
name|storeOffsets
decl_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|storePositions
condition|)
name|bits
operator||=
name|STORE_POSITIONS_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|storeOffsets
condition|)
name|bits
operator||=
name|STORE_OFFSET_WITH_TERMVECTOR
expr_stmt|;
name|tvf
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|String
name|lastTermText
init|=
literal|""
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
condition|;
name|i
operator|++
control|)
block|{
name|TVTerm
name|term
init|=
operator|(
name|TVTerm
operator|)
name|terms
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|lastTermText
argument_list|,
name|term
operator|.
name|termText
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|term
operator|.
name|termText
operator|.
name|length
argument_list|()
operator|-
name|start
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// write shared prefix length
name|tvf
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
comment|// write delta length
name|tvf
operator|.
name|writeChars
argument_list|(
name|term
operator|.
name|termText
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// write delta chars
name|tvf
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastTermText
operator|=
name|term
operator|.
name|termText
expr_stmt|;
if|if
condition|(
name|storePositions
condition|)
block|{
if|if
condition|(
name|term
operator|.
name|positions
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to write positions that are null!"
argument_list|)
throw|;
comment|// use delta encoding for positions
name|int
name|position
init|=
literal|0
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
name|term
operator|.
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|tvf
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|positions
index|[
name|j
index|]
operator|-
name|position
argument_list|)
expr_stmt|;
name|position
operator|=
name|term
operator|.
name|positions
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|storeOffsets
condition|)
block|{
if|if
condition|(
name|term
operator|.
name|offsets
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to write offsets that are null!"
argument_list|)
throw|;
comment|// use delta encoding for offsets
name|int
name|position
init|=
literal|0
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
name|term
operator|.
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|tvf
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|offsets
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
operator|-
name|position
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|offsets
index|[
name|j
index|]
operator|.
name|getEndOffset
argument_list|()
operator|-
name|term
operator|.
name|offsets
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
comment|//Save the diff between the two.
name|position
operator|=
name|term
operator|.
name|offsets
index|[
name|j
index|]
operator|.
name|getEndOffset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|writeDoc
specifier|private
name|void
name|writeDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isFieldOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Field is still open while writing document"
argument_list|)
throw|;
comment|//System.out.println("Writing doc pointer: " + currentDocPointer);
comment|// write document index record
name|tvx
operator|.
name|writeLong
argument_list|(
name|currentDocPointer
argument_list|)
expr_stmt|;
comment|// write document data record
specifier|final
name|int
name|size
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// write the number of fields
name|tvd
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
comment|// write field numbers
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
name|TVField
name|field
init|=
operator|(
name|TVField
operator|)
name|fields
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
block|}
comment|// write field pointers
name|long
name|lastFieldPointer
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|TVField
name|field
init|=
operator|(
name|TVField
operator|)
name|fields
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|tvd
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|tvfPointer
operator|-
name|lastFieldPointer
argument_list|)
expr_stmt|;
name|lastFieldPointer
operator|=
name|field
operator|.
name|tvfPointer
expr_stmt|;
block|}
comment|//System.out.println("After writing doc pointer: " + tvx.getFilePointer());
block|}
DECL|class|TVField
specifier|private
specifier|static
class|class
name|TVField
block|{
DECL|field|number
name|int
name|number
decl_stmt|;
DECL|field|tvfPointer
name|long
name|tvfPointer
init|=
literal|0
decl_stmt|;
DECL|field|storePositions
name|boolean
name|storePositions
init|=
literal|false
decl_stmt|;
DECL|field|storeOffsets
name|boolean
name|storeOffsets
init|=
literal|false
decl_stmt|;
DECL|method|TVField
name|TVField
parameter_list|(
name|int
name|number
parameter_list|,
name|boolean
name|storePos
parameter_list|,
name|boolean
name|storeOff
parameter_list|)
block|{
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|storePositions
operator|=
name|storePos
expr_stmt|;
name|storeOffsets
operator|=
name|storeOff
expr_stmt|;
block|}
block|}
DECL|class|TVTerm
specifier|private
specifier|static
class|class
name|TVTerm
block|{
DECL|field|termText
name|String
name|termText
decl_stmt|;
DECL|field|freq
name|int
name|freq
init|=
literal|0
decl_stmt|;
DECL|field|positions
name|int
name|positions
index|[]
init|=
literal|null
decl_stmt|;
DECL|field|offsets
name|TermVectorOffsetInfo
index|[]
name|offsets
init|=
literal|null
decl_stmt|;
block|}
block|}
end_class
end_unit
