begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|sep
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/** @lucene.experimental */
end_comment
begin_class
DECL|class|SingleIntFactory
specifier|public
class|class
name|SingleIntFactory
extends|extends
name|IntStreamFactory
block|{
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IntIndexInput
name|openInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleIntIndexInput
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|readBufferSize
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IntIndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleIntIndexOutput
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
return|;
block|}
block|}
end_class
end_unit
