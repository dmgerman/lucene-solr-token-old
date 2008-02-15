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
name|Directory
import|;
end_import
begin_comment
comment|/** This stores a monotonically increasing set of<Term, TermInfo> pairs in a   Directory.  A TermInfos can be written once, in order.  */
end_comment
begin_class
DECL|class|TermInfosWriter
specifier|final
class|class
name|TermInfosWriter
block|{
comment|/** The file format version, a negative number. */
DECL|field|FORMAT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT
init|=
operator|-
literal|3
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|output
specifier|private
name|IndexOutput
name|output
decl_stmt|;
DECL|field|lastTi
specifier|private
name|TermInfo
name|lastTi
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
decl_stmt|;
comment|// TODO: the default values for these two parameters should be settable from
comment|// IndexWriter.  However, once that's done, folks will start setting them to
comment|// ridiculous values and complaining that things don't work well, as with
comment|// mergeFactor.  So, let's wait until a number of folks find that alternate
comment|// values work better.  Note that both of these values are stored in the
comment|// segment, so that it's safe to change these w/o rebuilding all indexes.
comment|/** Expert: The fraction of terms in the "dictionary" which should be stored    * in RAM.  Smaller values use more memory, but make searching slightly    * faster, while larger values use less memory and make searching slightly    * slower.  Searching is typically not dominated by dictionary lookup, so    * tweaking this is rarely useful.*/
DECL|field|indexInterval
name|int
name|indexInterval
init|=
literal|128
decl_stmt|;
comment|/** Expert: The fraction of {@link TermDocs} entries stored in skip tables,    * used to accellerate {@link TermDocs#skipTo(int)}.  Larger values result in    * smaller indexes, greater acceleration, but fewer accelerable cases, while    * smaller values result in bigger indexes, less acceleration and more    * accelerable cases. More detailed experiments would be useful here. */
DECL|field|skipInterval
name|int
name|skipInterval
init|=
literal|16
decl_stmt|;
comment|/** Expert: The maximum number of skip levels. Smaller values result in     * slightly smaller indexes, but slower skipping in big posting lists.    */
DECL|field|maxSkipLevels
name|int
name|maxSkipLevels
init|=
literal|10
decl_stmt|;
DECL|field|lastIndexPointer
specifier|private
name|long
name|lastIndexPointer
decl_stmt|;
DECL|field|isIndex
specifier|private
name|boolean
name|isIndex
decl_stmt|;
DECL|field|lastTermText
specifier|private
name|char
index|[]
name|lastTermText
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
DECL|field|lastTermTextLength
specifier|private
name|int
name|lastTermTextLength
decl_stmt|;
DECL|field|lastFieldNumber
specifier|private
name|int
name|lastFieldNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|termTextBuffer
specifier|private
name|char
index|[]
name|termTextBuffer
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
DECL|field|other
specifier|private
name|TermInfosWriter
name|other
decl_stmt|;
DECL|method|TermInfosWriter
name|TermInfosWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|.
name|other
operator|=
name|this
expr_stmt|;
block|}
DECL|method|TermInfosWriter
specifier|private
name|TermInfosWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|,
name|boolean
name|isIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
name|isIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|,
name|boolean
name|isi
parameter_list|)
throws|throws
name|IOException
block|{
name|indexInterval
operator|=
name|interval
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
name|isIndex
operator|=
name|isi
expr_stmt|;
name|output
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
operator|(
name|isIndex
condition|?
literal|".tii"
else|:
literal|".tis"
operator|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|FORMAT
argument_list|)
expr_stmt|;
comment|// write format
name|output
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// leave space for size
name|output
operator|.
name|writeInt
argument_list|(
name|indexInterval
argument_list|)
expr_stmt|;
comment|// write indexInterval
name|output
operator|.
name|writeInt
argument_list|(
name|skipInterval
argument_list|)
expr_stmt|;
comment|// write skipInterval
name|output
operator|.
name|writeInt
argument_list|(
name|maxSkipLevels
argument_list|)
expr_stmt|;
comment|// write maxSkipLevels
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|length
init|=
name|term
operator|.
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|termTextBuffer
operator|.
name|length
operator|<
name|length
condition|)
name|termTextBuffer
operator|=
operator|new
name|char
index|[
call|(
name|int
call|)
argument_list|(
name|length
operator|*
literal|1.25
argument_list|)
index|]
expr_stmt|;
name|term
operator|.
name|text
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|length
argument_list|,
name|termTextBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|term
operator|.
name|field
argument_list|)
argument_list|,
name|termTextBuffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|ti
argument_list|)
expr_stmt|;
block|}
comment|// Currently used only by assert statement
DECL|method|compareToLastTerm
specifier|private
name|int
name|compareToLastTerm
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|char
index|[]
name|termText
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|lastFieldNumber
operator|!=
name|fieldNumber
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|lastFieldNumber
argument_list|)
operator|.
name|compareTo
argument_list|(
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|fieldNumber
argument_list|)
argument_list|)
decl_stmt|;
comment|// If there is a field named "" (empty string) then we
comment|// will get 0 on this comparison, yet, it's "OK".  But
comment|// it's not OK if two different field numbers map to
comment|// the same name.
if|if
condition|(
name|cmp
operator|!=
literal|0
operator|||
name|lastFieldNumber
operator|!=
operator|-
literal|1
condition|)
return|return
name|cmp
return|;
block|}
while|while
condition|(
name|pos
operator|<
name|length
operator|&&
name|pos
operator|<
name|lastTermTextLength
condition|)
block|{
specifier|final
name|char
name|c1
init|=
name|lastTermText
index|[
name|pos
index|]
decl_stmt|;
specifier|final
name|char
name|c2
init|=
name|termText
index|[
name|pos
operator|+
name|start
index|]
decl_stmt|;
if|if
condition|(
name|c1
operator|<
name|c2
condition|)
return|return
operator|-
literal|1
return|;
elseif|else
if|if
condition|(
name|c1
operator|>
name|c2
condition|)
return|return
literal|1
return|;
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|lastTermTextLength
condition|)
comment|// Last term was longer
return|return
literal|1
return|;
elseif|else
if|if
condition|(
name|pos
operator|<
name|length
condition|)
comment|// Last term was shorter
return|return
operator|-
literal|1
return|;
else|else
return|return
literal|0
return|;
block|}
comment|/** Adds a new<<fieldNumber, termText>, TermInfo> pair to the set.     Term must be lexicographically greater than all previous Terms added.     TermInfo pointers must be positive and greater than all previous.*/
DECL|method|add
name|void
name|add
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|char
index|[]
name|termText
parameter_list|,
name|int
name|termTextStart
parameter_list|,
name|int
name|termTextLength
parameter_list|,
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|compareToLastTerm
argument_list|(
name|fieldNumber
argument_list|,
name|termText
argument_list|,
name|termTextStart
argument_list|,
name|termTextLength
argument_list|)
operator|<
literal|0
operator|||
operator|(
name|isIndex
operator|&&
name|termTextLength
operator|==
literal|0
operator|&&
name|lastTermTextLength
operator|==
literal|0
operator|)
assert|;
assert|assert
name|ti
operator|.
name|freqPointer
operator|>=
name|lastTi
operator|.
name|freqPointer
operator|:
literal|"freqPointer out of order ("
operator|+
name|ti
operator|.
name|freqPointer
operator|+
literal|"< "
operator|+
name|lastTi
operator|.
name|freqPointer
operator|+
literal|")"
assert|;
assert|assert
name|ti
operator|.
name|proxPointer
operator|>=
name|lastTi
operator|.
name|proxPointer
operator|:
literal|"proxPointer out of order ("
operator|+
name|ti
operator|.
name|proxPointer
operator|+
literal|"< "
operator|+
name|lastTi
operator|.
name|proxPointer
operator|+
literal|")"
assert|;
if|if
condition|(
operator|!
name|isIndex
operator|&&
name|size
operator|%
name|indexInterval
operator|==
literal|0
condition|)
name|other
operator|.
name|add
argument_list|(
name|lastFieldNumber
argument_list|,
name|lastTermText
argument_list|,
literal|0
argument_list|,
name|lastTermTextLength
argument_list|,
name|lastTi
argument_list|)
expr_stmt|;
comment|// add an index term
name|writeTerm
argument_list|(
name|fieldNumber
argument_list|,
name|termText
argument_list|,
name|termTextStart
argument_list|,
name|termTextLength
argument_list|)
expr_stmt|;
comment|// write term
name|output
operator|.
name|writeVInt
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|)
expr_stmt|;
comment|// write doc freq
name|output
operator|.
name|writeVLong
argument_list|(
name|ti
operator|.
name|freqPointer
operator|-
name|lastTi
operator|.
name|freqPointer
argument_list|)
expr_stmt|;
comment|// write pointers
name|output
operator|.
name|writeVLong
argument_list|(
name|ti
operator|.
name|proxPointer
operator|-
name|lastTi
operator|.
name|proxPointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|>=
name|skipInterval
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|ti
operator|.
name|skipOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isIndex
condition|)
block|{
name|output
operator|.
name|writeVLong
argument_list|(
name|other
operator|.
name|output
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastIndexPointer
argument_list|)
expr_stmt|;
name|lastIndexPointer
operator|=
name|other
operator|.
name|output
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|// write pointer
block|}
if|if
condition|(
name|lastTermText
operator|.
name|length
operator|<
name|termTextLength
condition|)
name|lastTermText
operator|=
operator|new
name|char
index|[
call|(
name|int
call|)
argument_list|(
name|termTextLength
operator|*
literal|1.25
argument_list|)
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termText
argument_list|,
name|termTextStart
argument_list|,
name|lastTermText
argument_list|,
literal|0
argument_list|,
name|termTextLength
argument_list|)
expr_stmt|;
name|lastTermTextLength
operator|=
name|termTextLength
expr_stmt|;
name|lastFieldNumber
operator|=
name|fieldNumber
expr_stmt|;
name|lastTi
operator|.
name|set
argument_list|(
name|ti
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
DECL|method|writeTerm
specifier|private
name|void
name|writeTerm
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|char
index|[]
name|termText
parameter_list|,
name|int
name|termTextStart
parameter_list|,
name|int
name|termTextLength
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Compute prefix in common with last term:
name|int
name|start
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|termTextLength
operator|<
name|lastTermTextLength
condition|?
name|termTextLength
else|:
name|lastTermTextLength
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|limit
condition|)
block|{
if|if
condition|(
name|termText
index|[
name|termTextStart
operator|+
name|start
index|]
operator|!=
name|lastTermText
index|[
name|start
index|]
condition|)
break|break;
name|start
operator|++
expr_stmt|;
block|}
name|int
name|length
init|=
name|termTextLength
operator|-
name|start
decl_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// write shared prefix length
name|output
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
comment|// write delta length
name|output
operator|.
name|writeChars
argument_list|(
name|termText
argument_list|,
name|start
operator|+
name|termTextStart
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// write delta chars
name|output
operator|.
name|writeVInt
argument_list|(
name|fieldNumber
argument_list|)
expr_stmt|;
comment|// write field num
block|}
comment|/** Called to complete TermInfos creation. */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|output
operator|.
name|seek
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// write size after format
name|output
operator|.
name|writeLong
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isIndex
condition|)
name|other
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
