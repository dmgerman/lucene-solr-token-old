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
name|store
operator|.
name|IndexInput
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_class
DECL|class|SegmentInfo
specifier|final
class|class
name|SegmentInfo
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
comment|// unique name in dir
DECL|field|docCount
specifier|public
name|int
name|docCount
decl_stmt|;
comment|// number of docs in seg
DECL|field|dir
specifier|public
name|Directory
name|dir
decl_stmt|;
comment|// where segment resides
DECL|field|preLockless
specifier|private
name|boolean
name|preLockless
decl_stmt|;
comment|// true if this is a segments file written before
comment|// lock-less commits (2.1)
DECL|field|delGen
specifier|private
name|long
name|delGen
decl_stmt|;
comment|// current generation of del file; -1 if there
comment|// are no deletes; 0 if it's a pre-2.1 segment
comment|// (and we must check filesystem); 1 or higher if
comment|// there are deletes at generation N
DECL|field|normGen
specifier|private
name|long
index|[]
name|normGen
decl_stmt|;
comment|// current generations of each field's norm file.
comment|// If this array is null, we must check filesystem
comment|// when preLockLess is true.  Else,
comment|// there are no separate norms
DECL|field|isCompoundFile
specifier|private
name|byte
name|isCompoundFile
decl_stmt|;
comment|// -1 if it is not; 1 if it is; 0 if it's
comment|// pre-2.1 (ie, must check file system to see
comment|// if<name>.cfs and<name>.nrm exist)
DECL|field|hasSingleNormFile
specifier|private
name|boolean
name|hasSingleNormFile
decl_stmt|;
comment|// true if this segment maintains norms in a single file;
comment|// false otherwise
comment|// this is currently false for segments populated by DocumentWriter
comment|// and true for newly created merged segments (both
comment|// compound and non compound).
DECL|field|files
specifier|private
name|List
name|files
decl_stmt|;
comment|// cached list of files that this segment uses
comment|// in the Directory
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|delGen
operator|=
operator|-
literal|1
expr_stmt|;
name|isCompoundFile
operator|=
literal|0
expr_stmt|;
name|preLockless
operator|=
literal|true
expr_stmt|;
name|hasSingleNormFile
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|SegmentInfo
specifier|public
name|SegmentInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|boolean
name|isCompoundFile
parameter_list|,
name|boolean
name|hasSingleNormFile
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|docCount
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|isCompoundFile
operator|=
call|(
name|byte
call|)
argument_list|(
name|isCompoundFile
condition|?
literal|1
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasSingleNormFile
operator|=
name|hasSingleNormFile
expr_stmt|;
name|preLockless
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Copy everything from src SegmentInfo into our instance.    */
DECL|method|reset
name|void
name|reset
parameter_list|(
name|SegmentInfo
name|src
parameter_list|)
block|{
name|files
operator|=
literal|null
expr_stmt|;
name|name
operator|=
name|src
operator|.
name|name
expr_stmt|;
name|docCount
operator|=
name|src
operator|.
name|docCount
expr_stmt|;
name|dir
operator|=
name|src
operator|.
name|dir
expr_stmt|;
name|preLockless
operator|=
name|src
operator|.
name|preLockless
expr_stmt|;
name|delGen
operator|=
name|src
operator|.
name|delGen
expr_stmt|;
if|if
condition|(
name|src
operator|.
name|normGen
operator|==
literal|null
condition|)
block|{
name|normGen
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|normGen
operator|=
operator|new
name|long
index|[
name|src
operator|.
name|normGen
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|src
operator|.
name|normGen
argument_list|,
literal|0
argument_list|,
name|normGen
argument_list|,
literal|0
argument_list|,
name|src
operator|.
name|normGen
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|isCompoundFile
operator|=
name|src
operator|.
name|isCompoundFile
expr_stmt|;
name|hasSingleNormFile
operator|=
name|src
operator|.
name|hasSingleNormFile
expr_stmt|;
block|}
comment|/**    * Construct a new SegmentInfo instance by reading a    * previously saved SegmentInfo from input.    *    * @param dir directory to load from    * @param format format of the segments info file    * @param input input handle to read segment info from    */
DECL|method|SegmentInfo
name|SegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|format
parameter_list|,
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|name
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
name|docCount
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|format
operator|<=
name|SegmentInfos
operator|.
name|FORMAT_LOCKLESS
condition|)
block|{
name|delGen
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|format
operator|<=
name|SegmentInfos
operator|.
name|FORMAT_SINGLE_NORM_FILE
condition|)
block|{
name|hasSingleNormFile
operator|=
operator|(
literal|1
operator|==
name|input
operator|.
name|readByte
argument_list|()
operator|)
expr_stmt|;
block|}
else|else
block|{
name|hasSingleNormFile
operator|=
literal|false
expr_stmt|;
block|}
name|int
name|numNormGen
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numNormGen
operator|==
operator|-
literal|1
condition|)
block|{
name|normGen
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|normGen
operator|=
operator|new
name|long
index|[
name|numNormGen
index|]
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
name|numNormGen
condition|;
name|j
operator|++
control|)
block|{
name|normGen
index|[
name|j
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
name|isCompoundFile
operator|=
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|preLockless
operator|=
name|isCompoundFile
operator|==
literal|0
expr_stmt|;
block|}
else|else
block|{
name|delGen
operator|=
literal|0
expr_stmt|;
name|normGen
operator|=
literal|null
expr_stmt|;
name|isCompoundFile
operator|=
literal|0
expr_stmt|;
name|preLockless
operator|=
literal|true
expr_stmt|;
name|hasSingleNormFile
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|setNumFields
name|void
name|setNumFields
parameter_list|(
name|int
name|numFields
parameter_list|)
block|{
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
comment|// normGen is null if we loaded a pre-2.1 segment
comment|// file, or, if this segments file hasn't had any
comment|// norms set against it yet:
name|normGen
operator|=
operator|new
name|long
index|[
name|numFields
index|]
expr_stmt|;
if|if
condition|(
operator|!
name|preLockless
condition|)
block|{
comment|// This is a FORMAT_LOCKLESS segment, which means
comment|// there are no separate norms:
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
name|normGen
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|hasDeletions
name|boolean
name|hasDeletions
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Cases:
comment|//
comment|//   delGen == -1: this means this segment was written
comment|//     by the LOCKLESS code and for certain does not have
comment|//     deletions yet
comment|//
comment|//   delGen == 0: this means this segment was written by
comment|//     pre-LOCKLESS code which means we must check
comment|//     directory to see if .del file exists
comment|//
comment|//   delGen> 0: this means this segment was written by
comment|//     the LOCKLESS code and for certain has
comment|//     deletions
comment|//
if|if
condition|(
name|delGen
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|delGen
operator|>
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|dir
operator|.
name|fileExists
argument_list|(
name|getDelFileName
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|advanceDelGen
name|void
name|advanceDelGen
parameter_list|()
block|{
comment|// delGen 0 is reserved for pre-LOCKLESS format
if|if
condition|(
name|delGen
operator|==
operator|-
literal|1
condition|)
block|{
name|delGen
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|delGen
operator|++
expr_stmt|;
block|}
name|files
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|clearDelGen
name|void
name|clearDelGen
parameter_list|()
block|{
name|delGen
operator|=
operator|-
literal|1
expr_stmt|;
name|files
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|SegmentInfo
name|si
init|=
operator|new
name|SegmentInfo
argument_list|(
name|name
argument_list|,
name|docCount
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|si
operator|.
name|isCompoundFile
operator|=
name|isCompoundFile
expr_stmt|;
name|si
operator|.
name|delGen
operator|=
name|delGen
expr_stmt|;
name|si
operator|.
name|preLockless
operator|=
name|preLockless
expr_stmt|;
name|si
operator|.
name|hasSingleNormFile
operator|=
name|hasSingleNormFile
expr_stmt|;
if|if
condition|(
name|normGen
operator|!=
literal|null
condition|)
block|{
name|si
operator|.
name|normGen
operator|=
operator|(
name|long
index|[]
operator|)
name|normGen
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|si
return|;
block|}
DECL|method|getDelFileName
name|String
name|getDelFileName
parameter_list|()
block|{
if|if
condition|(
name|delGen
operator|==
operator|-
literal|1
condition|)
block|{
comment|// In this case we know there is no deletion filename
comment|// against this segment
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// If delGen is 0, it's the pre-lockless-commit file format
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
literal|"."
operator|+
name|IndexFileNames
operator|.
name|DELETES_EXTENSION
argument_list|,
name|delGen
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns true if this field for this segment has saved a separate norms file (_<segment>_N.sX).    *    * @param fieldNumber the field index to check    */
DECL|method|hasSeparateNorms
name|boolean
name|hasSeparateNorms
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|normGen
operator|==
literal|null
operator|&&
name|preLockless
operator|)
operator|||
operator|(
name|normGen
operator|!=
literal|null
operator|&&
name|normGen
index|[
name|fieldNumber
index|]
operator|==
literal|0
operator|)
condition|)
block|{
comment|// Must fallback to directory file exists check:
name|String
name|fileName
init|=
name|name
operator|+
literal|".s"
operator|+
name|fieldNumber
decl_stmt|;
return|return
name|dir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|normGen
operator|==
literal|null
operator|||
name|normGen
index|[
name|fieldNumber
index|]
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Returns true if any fields in this segment have separate norms.    */
DECL|method|hasSeparateNorms
name|boolean
name|hasSeparateNorms
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|preLockless
condition|)
block|{
comment|// This means we were created w/ LOCKLESS code and no
comment|// norms are written yet:
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// This means this segment was saved with pre-LOCKLESS
comment|// code.  So we must fallback to the original
comment|// directory list check:
name|String
index|[]
name|result
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot read directory "
operator|+
name|dir
operator|+
literal|": list() returned null"
argument_list|)
throw|;
name|String
name|pattern
decl_stmt|;
name|pattern
operator|=
name|name
operator|+
literal|".s"
expr_stmt|;
name|int
name|patternLength
init|=
name|pattern
operator|.
name|length
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
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|result
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
name|pattern
argument_list|)
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|result
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
name|patternLength
argument_list|)
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// This means this segment was saved with LOCKLESS
comment|// code so we first check whether any normGen's are>
comment|// 0 (meaning they definitely have separate norms):
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|normGen
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|normGen
index|[
name|i
index|]
operator|>
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// Next we look for any == 0.  These cases were
comment|// pre-LOCKLESS and must be checked in directory:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|normGen
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|normGen
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|hasSeparateNorms
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Increment the generation count for the norms file for    * this field.    *    * @param fieldIndex field whose norm file will be rewritten    */
DECL|method|advanceNormGen
name|void
name|advanceNormGen
parameter_list|(
name|int
name|fieldIndex
parameter_list|)
block|{
if|if
condition|(
name|normGen
index|[
name|fieldIndex
index|]
operator|==
operator|-
literal|1
condition|)
block|{
name|normGen
index|[
name|fieldIndex
index|]
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|normGen
index|[
name|fieldIndex
index|]
operator|++
expr_stmt|;
block|}
name|files
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Get the file name for the norms file for this field.    *    * @param number field index    */
DECL|method|getNormFileName
name|String
name|getNormFileName
parameter_list|(
name|int
name|number
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|prefix
decl_stmt|;
name|long
name|gen
decl_stmt|;
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
name|gen
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|gen
operator|=
name|normGen
index|[
name|number
index|]
expr_stmt|;
block|}
if|if
condition|(
name|hasSeparateNorms
argument_list|(
name|number
argument_list|)
condition|)
block|{
comment|// case 1: separate norm
name|prefix
operator|=
literal|".s"
expr_stmt|;
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|prefix
operator|+
name|number
argument_list|,
name|gen
argument_list|)
return|;
block|}
if|if
condition|(
name|hasSingleNormFile
condition|)
block|{
comment|// case 2: lockless (or nrm file exists) - single file for all norms
name|prefix
operator|=
literal|"."
operator|+
name|IndexFileNames
operator|.
name|NORMS_EXTENSION
expr_stmt|;
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|prefix
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|// case 3: norm file for each field
name|prefix
operator|=
literal|".f"
expr_stmt|;
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
name|prefix
operator|+
name|number
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Mark whether this segment is stored as a compound file.    *    * @param isCompoundFile true if this is a compound file;    * else, false    */
DECL|method|setUseCompoundFile
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|isCompoundFile
parameter_list|)
block|{
if|if
condition|(
name|isCompoundFile
condition|)
block|{
name|this
operator|.
name|isCompoundFile
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|isCompoundFile
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|files
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Returns true if this segment is stored as a compound    * file; else, false.    */
DECL|method|getUseCompoundFile
name|boolean
name|getUseCompoundFile
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isCompoundFile
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|isCompoundFile
operator|==
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|dir
operator|.
name|fileExists
argument_list|(
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
return|;
block|}
block|}
comment|/**    * Save this segment's info.    */
DECL|method|write
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
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|delGen
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|hasSingleNormFile
condition|?
literal|1
else|:
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
name|output
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|normGen
operator|.
name|length
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
name|normGen
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|output
operator|.
name|writeLong
argument_list|(
name|normGen
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|output
operator|.
name|writeByte
argument_list|(
name|isCompoundFile
argument_list|)
expr_stmt|;
block|}
comment|/*    * Return all files referenced by this SegmentInfo.  The    * returns List is a locally cached List so you should not    * modify it.    */
DECL|method|files
specifier|public
name|List
name|files
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
comment|// Already cached:
return|return
name|files
return|;
block|}
name|files
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|boolean
name|useCompoundFile
init|=
name|getUseCompoundFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
expr_stmt|;
block|}
else|else
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
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS_IN_COMPOUND_FILE
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|ext
init|=
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS_IN_COMPOUND_FILE
index|[
name|i
index|]
decl_stmt|;
name|String
name|fileName
init|=
name|name
operator|+
literal|"."
operator|+
name|ext
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|delFileName
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
literal|"."
operator|+
name|IndexFileNames
operator|.
name|DELETES_EXTENSION
argument_list|,
name|delGen
argument_list|)
decl_stmt|;
if|if
condition|(
name|delFileName
operator|!=
literal|null
operator|&&
operator|(
name|delGen
operator|>
literal|0
operator|||
name|dir
operator|.
name|fileExists
argument_list|(
name|delFileName
argument_list|)
operator|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|delFileName
argument_list|)
expr_stmt|;
block|}
comment|// Careful logic for norms files:
if|if
condition|(
name|normGen
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
name|normGen
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|gen
init|=
name|normGen
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|gen
operator|>
literal|0
condition|)
block|{
comment|// Definitely a separate norm file, with generation:
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|name
argument_list|,
literal|"."
operator|+
name|IndexFileNames
operator|.
name|SEPARATE_NORMS_EXTENSION
operator|+
name|i
argument_list|,
name|gen
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|-
literal|1
operator|==
name|gen
condition|)
block|{
comment|// No separate norms but maybe non-separate norms
comment|// in the non compound file case:
if|if
condition|(
operator|!
name|hasSingleNormFile
operator|&&
operator|!
name|useCompoundFile
condition|)
block|{
name|String
name|fileName
init|=
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|SINGLE_NORMS_EXTENSION
operator|+
name|i
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|0
operator|==
name|gen
condition|)
block|{
comment|// Pre-2.1: we have to check file existence
name|String
name|fileName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|fileName
operator|=
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|SEPARATE_NORMS_EXTENSION
operator|+
name|i
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|hasSingleNormFile
condition|)
block|{
name|fileName
operator|=
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|SINGLE_NORMS_EXTENSION
operator|+
name|i
expr_stmt|;
block|}
if|if
condition|(
name|fileName
operator|!=
literal|null
operator|&&
name|dir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|preLockless
operator|||
operator|(
operator|!
name|hasSingleNormFile
operator|&&
operator|!
name|useCompoundFile
operator|)
condition|)
block|{
comment|// Pre-2.1: we have to scan the dir to find all
comment|// matching _X.sN/_X.fN files for our segment:
name|String
name|prefix
decl_stmt|;
if|if
condition|(
name|useCompoundFile
condition|)
name|prefix
operator|=
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|SEPARATE_NORMS_EXTENSION
expr_stmt|;
else|else
name|prefix
operator|=
name|name
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|SINGLE_NORMS_EXTENSION
expr_stmt|;
name|int
name|prefixLength
init|=
name|prefix
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
index|[]
name|allFiles
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|allFiles
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot read directory "
operator|+
name|dir
operator|+
literal|": list() returned null"
argument_list|)
throw|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
name|allFiles
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|length
argument_list|()
operator|>
name|prefixLength
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|fileName
operator|.
name|charAt
argument_list|(
name|prefixLength
argument_list|)
argument_list|)
operator|&&
name|fileName
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|files
return|;
block|}
block|}
end_class
end_unit
