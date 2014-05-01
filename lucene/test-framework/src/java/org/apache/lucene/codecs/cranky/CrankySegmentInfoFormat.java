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
name|SegmentInfoFormat
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
name|SegmentInfoReader
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
name|SegmentInfoWriter
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
begin_class
DECL|class|CrankySegmentInfoFormat
class|class
name|CrankySegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
DECL|field|delegate
specifier|final
name|SegmentInfoFormat
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankySegmentInfoFormat
name|CrankySegmentInfoFormat
parameter_list|(
name|SegmentInfoFormat
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
DECL|method|getSegmentInfoReader
specifier|public
name|SegmentInfoReader
name|getSegmentInfoReader
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSegmentInfoReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentInfoWriter
specifier|public
name|SegmentInfoWriter
name|getSegmentInfoWriter
parameter_list|()
block|{
return|return
operator|new
name|CrankySegmentInfoWriter
argument_list|(
name|delegate
operator|.
name|getSegmentInfoWriter
argument_list|()
argument_list|,
name|random
argument_list|)
return|;
block|}
DECL|class|CrankySegmentInfoWriter
specifier|static
class|class
name|CrankySegmentInfoWriter
extends|extends
name|SegmentInfoWriter
block|{
DECL|field|delegate
specifier|final
name|SegmentInfoWriter
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankySegmentInfoWriter
name|CrankySegmentInfoWriter
parameter_list|(
name|SegmentInfoWriter
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
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|IOContext
name|ioContext
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
literal|"Fake IOException from SegmentInfoWriter.write()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|fis
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
