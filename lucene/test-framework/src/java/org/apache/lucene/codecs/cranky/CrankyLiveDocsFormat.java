begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.cranky
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|cranky
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
name|Random
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
name|LiveDocsFormat
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
name|SegmentCommitInfo
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
name|MutableBits
import|;
end_import
begin_class
DECL|class|CrankyLiveDocsFormat
class|class
name|CrankyLiveDocsFormat
extends|extends
name|LiveDocsFormat
block|{
DECL|field|delegate
specifier|final
name|LiveDocsFormat
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyLiveDocsFormat
name|CrankyLiveDocsFormat
parameter_list|(
name|LiveDocsFormat
name|delegate
parameter_list|,
name|Random
name|random
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
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newLiveDocs
argument_list|(
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|Bits
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newLiveDocs
argument_list|(
name|existing
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readLiveDocs
specifier|public
name|Bits
name|readLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentCommitInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|readLiveDocs
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeLiveDocs
specifier|public
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentCommitInfo
name|info
parameter_list|,
name|int
name|newDelCount
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from LiveDocsFormat.writeLiveDocs()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|writeLiveDocs
argument_list|(
name|bits
argument_list|,
name|dir
argument_list|,
name|info
argument_list|,
name|newDelCount
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: is this called only from write? if so we should throw exception!
name|delegate
operator|.
name|files
argument_list|(
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
