begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|FieldsConsumer
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
name|PostingsWriterBase
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
name|blocktree
operator|.
name|BlockTreeTermsWriter
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
name|SegmentWriteState
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
begin_comment
comment|/**  * Read-write version of {@link Lucene40PostingsFormat} for testing.  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|Lucene40RWPostingsFormat
specifier|public
class|class
name|Lucene40RWPostingsFormat
extends|extends
name|Lucene40PostingsFormat
block|{
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|LuceneTestCase
operator|.
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|super
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
else|else
block|{
name|PostingsWriterBase
name|docs
init|=
operator|new
name|Lucene40PostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// TODO: should we make the terms index more easily
comment|// pluggable?  Ie so that this codec would record which
comment|// index impl was used, and switch on loading?
comment|// Or... you must make a new Codec for this?
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|docs
argument_list|,
name|minBlockSize
argument_list|,
name|maxBlockSize
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
