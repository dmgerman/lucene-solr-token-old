begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|index
operator|.
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
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
name|AttributeSource
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
name|FloatsRef
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
name|LongsRef
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
name|ReaderUtil
operator|.
name|Slice
import|;
end_import
begin_class
DECL|class|MultiDocValues
specifier|public
class|class
name|MultiDocValues
extends|extends
name|DocValues
block|{
DECL|class|DocValuesIndex
specifier|public
specifier|static
class|class
name|DocValuesIndex
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|DocValuesIndex
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|DocValuesIndex
index|[
literal|0
index|]
decl_stmt|;
DECL|field|subIndex
specifier|final
name|int
name|subIndex
decl_stmt|;
DECL|field|docValues
specifier|final
name|DocValues
name|docValues
decl_stmt|;
DECL|method|DocValuesIndex
specifier|public
name|DocValuesIndex
parameter_list|(
name|DocValues
name|docValues
parameter_list|,
name|int
name|subIndex
parameter_list|)
block|{
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
name|this
operator|.
name|subIndex
operator|=
name|subIndex
expr_stmt|;
block|}
block|}
DECL|field|docValuesIdx
specifier|private
name|DocValuesIndex
index|[]
name|docValuesIdx
decl_stmt|;
DECL|field|subSlices
specifier|private
name|Slice
index|[]
name|subSlices
decl_stmt|;
DECL|method|MultiDocValues
specifier|public
name|MultiDocValues
parameter_list|(
name|Slice
index|[]
name|subSlices
parameter_list|)
block|{
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
block|}
DECL|method|MultiDocValues
specifier|public
name|MultiDocValues
parameter_list|(
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|,
name|Slice
index|[]
name|subSlices
parameter_list|)
block|{
name|this
argument_list|(
name|subSlices
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|docValuesIdx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiValuesEnum
argument_list|(
name|subSlices
argument_list|,
name|docValuesIdx
argument_list|,
name|docValuesIdx
index|[
literal|0
index|]
operator|.
name|docValues
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiSource
argument_list|(
name|subSlices
argument_list|,
name|docValuesIdx
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//
block|}
DECL|method|reset
specifier|public
name|DocValues
name|reset
parameter_list|(
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|)
block|{
name|this
operator|.
name|docValuesIdx
operator|=
name|docValuesIdx
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|class|MultiValuesEnum
specifier|private
specifier|static
class|class
name|MultiValuesEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|numDocs_
specifier|private
name|int
name|numDocs_
init|=
literal|0
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
init|=
literal|0
decl_stmt|;
DECL|field|current
specifier|private
name|ValuesEnum
name|current
decl_stmt|;
DECL|field|subSlices
specifier|private
name|Slice
index|[]
name|subSlices
decl_stmt|;
DECL|field|docValuesIdx
specifier|private
name|DocValuesIndex
index|[]
name|docValuesIdx
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|MultiValuesEnum
specifier|public
name|MultiValuesEnum
parameter_list|(
name|Slice
index|[]
name|subSlices
parameter_list|,
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|,
name|Values
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
name|this
operator|.
name|docValuesIdx
operator|=
name|docValuesIdx
expr_stmt|;
name|Slice
name|slice
init|=
name|subSlices
index|[
name|subSlices
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|maxDoc
operator|=
name|slice
operator|.
name|start
operator|+
name|slice
operator|.
name|length
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
block|{            }
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|//      int n = target - start;
comment|//      do {
comment|//        if (target>= maxDoc)
comment|//          return pos = NO_MORE_DOCS;
comment|//        if (n>= numDocs_) {
comment|//          int idx = readerIndex(target);
comment|//          if (enumCache[idx] == null) {
comment|//            try {
comment|//              DocValues indexValues = subReaders[idx].docValues(id);
comment|//              if (indexValues != null) // nocommit does that work with default
comment|//                // values?
comment|//                enumCache[idx] = indexValues.getEnum(this.attributes());
comment|//              else
comment|//                enumCache[idx] = new DummyEnum(this.attributes(),
comment|//                    subSlices[idx].length, attr.type());
comment|//            } catch (IOException ex) {
comment|//              // nocommit what to do here?
comment|//              throw new RuntimeException(ex);
comment|//            }
comment|//          }
comment|//          current = enumCache[idx];
comment|//          start = subSlices[idx].start;
comment|//          numDocs_ = subSlices[idx].length;
comment|//          n = target - start;
comment|//        }
comment|//        target = start + numDocs_;
comment|//      } while ((n = current.advance(n)) == NO_MORE_DOCS);
return|return
name|pos
operator|=
name|start
operator|+
name|current
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
DECL|class|MultiSource
specifier|private
class|class
name|MultiSource
extends|extends
name|Source
block|{
DECL|field|numDocs_
specifier|private
name|int
name|numDocs_
init|=
literal|0
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
init|=
literal|0
decl_stmt|;
DECL|field|current
specifier|private
name|Source
name|current
decl_stmt|;
DECL|field|subSlices
specifier|private
name|Slice
index|[]
name|subSlices
decl_stmt|;
DECL|field|docVAluesIdx
specifier|private
name|DocValuesIndex
index|[]
name|docVAluesIdx
decl_stmt|;
DECL|method|MultiSource
specifier|public
name|MultiSource
parameter_list|(
name|Slice
index|[]
name|subSlices
parameter_list|,
name|DocValuesIndex
index|[]
name|docValuesIdx
parameter_list|)
block|{
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
name|this
operator|.
name|docVAluesIdx
operator|=
name|docValuesIdx
expr_stmt|;
block|}
DECL|method|ints
specifier|public
name|long
name|ints
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|//      int n = docID - start;
comment|//      if (n>= numDocs_) {
comment|//        int idx = readerIndex(docID);
comment|//        try {
comment|//          current = subReaders[idx].getIndexValuesCache().getInts(id);
comment|//          if (current == null) // nocommit does that work with default values?
comment|//            current = new DummySource();
comment|//        } catch (IOException ex) {
comment|//          // nocommit what to do here?
comment|//          throw new RuntimeException(ex);
comment|//        }
comment|//        start = starts[idx];
comment|//        numDocs_ = subReaders[idx].maxDoc();
comment|//        n = docID - start;
comment|//      }
comment|//      return current.ints(n);
return|return
literal|0l
return|;
block|}
DECL|method|floats
specifier|public
name|double
name|floats
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|//      int n = docID - start;
comment|//      if (n>= numDocs_) {
comment|//        int idx = readerIndex(docID);
comment|//        try {
comment|//          current = subReaders[idx].getIndexValuesCache().getFloats(id);
comment|//          if (current == null) // nocommit does that work with default values?
comment|//            current = new DummySource();
comment|//        } catch (IOException ex) {
comment|//          // nocommit what to do here?
comment|//          throw new RuntimeException(ex);
comment|//        }
comment|//        numDocs_ = subReaders[idx].maxDoc();
comment|//
comment|//        start = starts[idx];
comment|//        n = docID - start;
comment|//      }
comment|//      return current.floats(n);
return|return
literal|0d
return|;
block|}
DECL|method|bytes
specifier|public
name|BytesRef
name|bytes
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|//      int n = docID - start;
comment|//      if (n>= numDocs_) {
comment|//        int idx = readerIndex(docID);
comment|//        try {
comment|//          current = subReaders[idx].getIndexValuesCache().getBytes(id);
comment|//          if (current == null) // nocommit does that work with default values?
comment|//            current = new DummySource();
comment|//        } catch (IOException ex) {
comment|//          // nocommit what to do here?
comment|//          throw new RuntimeException(ex);
comment|//        }
comment|//        numDocs_ = subReaders[idx].maxDoc();
comment|//        start = starts[idx];
comment|//        n = docID - start;
comment|//      }
comment|//      return current.bytes(n);
return|return
literal|null
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|current
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
block|}
DECL|class|DummySource
specifier|private
specifier|static
class|class
name|DummySource
extends|extends
name|Source
block|{
DECL|field|ref
specifier|private
specifier|final
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|bytes
specifier|public
name|BytesRef
name|bytes
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|ref
return|;
block|}
annotation|@
name|Override
DECL|method|floats
specifier|public
name|double
name|floats
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0.0d
return|;
block|}
annotation|@
name|Override
DECL|method|ints
specifier|public
name|long
name|ints
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|class|DummyEnum
specifier|private
specifier|static
class|class
name|DummyEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|DummyEnum
specifier|public
name|DummyEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Values
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
comment|// nocommit - this is not correct for Fixed_straight
name|BytesRef
name|bytes
init|=
name|attr
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|bytes
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|bytes
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
break|break;
case|case
name|PACKED_INTS
case|:
case|case
name|PACKED_INTS_FIXED
case|:
name|LongsRef
name|ints
init|=
name|attr
operator|.
name|ints
argument_list|()
decl_stmt|;
name|ints
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
name|FloatsRef
name|floats
init|=
name|attr
operator|.
name|floats
argument_list|()
decl_stmt|;
name|floats
operator|.
name|set
argument_list|(
literal|0d
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown Values type: "
operator|+
name|type
argument_list|)
throw|;
block|}
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
block|{     }
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pos
operator|=
operator|(
name|pos
operator|<
name|maxDoc
condition|?
name|target
else|:
name|NO_MORE_DOCS
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|docValuesIdx
index|[
literal|0
index|]
operator|.
name|docValues
operator|.
name|type
argument_list|()
return|;
block|}
block|}
end_class
end_unit
