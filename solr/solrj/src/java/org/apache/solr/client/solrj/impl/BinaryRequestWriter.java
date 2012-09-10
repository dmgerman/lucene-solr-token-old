begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|JavaBinUpdateRequestCodec
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|RequestWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|List
import|;
end_import
begin_comment
comment|/**  * A RequestWriter which writes requests in the javabin format  *  *  * @see org.apache.solr.client.solrj.request.RequestWriter  * @since solr 1.4  */
end_comment
begin_class
DECL|class|BinaryRequestWriter
specifier|public
class|class
name|BinaryRequestWriter
extends|extends
name|RequestWriter
block|{
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|(
name|SolrRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|req
operator|instanceof
name|UpdateRequest
condition|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|req
decl_stmt|;
if|if
condition|(
name|isNull
argument_list|(
name|updateRequest
operator|.
name|getDocuments
argument_list|()
argument_list|)
operator|&&
name|isNull
argument_list|(
name|updateRequest
operator|.
name|getDeleteById
argument_list|()
argument_list|)
operator|&&
name|isNull
argument_list|(
name|updateRequest
operator|.
name|getDeleteQuery
argument_list|()
argument_list|)
operator|&&
operator|(
name|updateRequest
operator|.
name|getDocIterator
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|ContentStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|LazyContentStream
argument_list|(
name|updateRequest
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getContentStreams
argument_list|(
name|req
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUpdateContentType
specifier|public
name|String
name|getUpdateContentType
parameter_list|()
block|{
return|return
literal|"application/javabin"
return|;
block|}
annotation|@
name|Override
DECL|method|getContentStream
specifier|public
name|ContentStream
name|getContentStream
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BAOS
name|baos
init|=
operator|new
name|BAOS
argument_list|()
decl_stmt|;
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
operator|.
name|marshal
argument_list|(
name|request
argument_list|,
name|baos
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContentStream
argument_list|()
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getSourceInfo
parameter_list|()
block|{
return|return
literal|"javabin"
return|;
block|}
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
literal|"application/javabin"
return|;
block|}
specifier|public
name|Long
name|getSize
parameter_list|()
comment|// size if we know it, otherwise null
block|{
return|return
operator|new
name|Long
argument_list|(
name|baos
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|getStream
parameter_list|()
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|getbuf
argument_list|()
argument_list|,
literal|0
argument_list|,
name|baos
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Reader
name|getReader
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No reader available . this is a binarystream"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|SolrRequest
name|request
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|request
operator|instanceof
name|UpdateRequest
condition|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|request
decl_stmt|;
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
operator|.
name|marshal
argument_list|(
name|updateRequest
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * A hack to get access to the protected internal buffer and avoid an additional copy    */
DECL|class|BAOS
class|class
name|BAOS
extends|extends
name|ByteArrayOutputStream
block|{
DECL|method|getbuf
name|byte
index|[]
name|getbuf
parameter_list|()
block|{
return|return
name|super
operator|.
name|buf
return|;
block|}
block|}
block|}
end_class
end_unit
