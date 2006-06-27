begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
package|;
end_package
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
name|io
operator|.
name|StringWriter
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
name|Field
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseFeed
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedService
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|common
operator|.
name|xml
operator|.
name|XmlWriter
import|;
end_import
begin_comment
comment|/**  * This immutable class wrapps<tt>GDataAccount</tt> instances for an internal Storage representation of   * an account. This class also acts as a Documentfactory for lucene documents to   * be stored inside the index.   * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|StorageFeedWrapper
specifier|public
class|class
name|StorageFeedWrapper
implements|implements
name|StorageWrapper
block|{
DECL|field|INTERNAL_ENCODING
specifier|private
specifier|static
specifier|final
name|String
name|INTERNAL_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
comment|/**      * the account who owns the feed       */
DECL|field|FIELD_ACCOUNTREFERENCE
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_ACCOUNTREFERENCE
init|=
literal|"accountReference"
decl_stmt|;
comment|/**      * the id of the feed      */
DECL|field|FIELD_FEED_ID
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_FEED_ID
init|=
literal|"feedId"
decl_stmt|;
comment|/**      * The xml feed representation      */
DECL|field|FIELD_CONTENT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_CONTENT
init|=
literal|"content"
decl_stmt|;
comment|/**      * The creation timestamp      */
DECL|field|FIELD_TIMESTAMP
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_TIMESTAMP
init|=
literal|"timestamp"
decl_stmt|;
comment|/**      * The Service this feed belongs to.       */
DECL|field|FIELD_SERVICE_ID
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_SERVICE_ID
init|=
literal|"serviceId"
decl_stmt|;
DECL|field|feed
specifier|private
specifier|final
name|ServerBaseFeed
name|feed
decl_stmt|;
DECL|field|accountName
specifier|private
specifier|final
name|String
name|accountName
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|ProvidedService
name|config
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|String
name|content
decl_stmt|;
comment|/**      * @param feed       * @param accountname       * @throws IOException       *       */
DECL|method|StorageFeedWrapper
specifier|public
name|StorageFeedWrapper
parameter_list|(
specifier|final
name|ServerBaseFeed
name|feed
parameter_list|,
specifier|final
name|String
name|accountname
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|feed
operator|=
name|feed
expr_stmt|;
name|this
operator|.
name|accountName
operator|=
name|accountname
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|feed
operator|.
name|getServiceConfig
argument_list|()
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|buildContent
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageWrapper#getLuceneDocument()      */
DECL|method|getLuceneDocument
specifier|public
name|Document
name|getLuceneDocument
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_ACCOUNTREFERENCE
argument_list|,
name|this
operator|.
name|accountName
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_FEED_ID
argument_list|,
name|this
operator|.
name|feed
operator|.
name|getId
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_CONTENT
argument_list|,
name|this
operator|.
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_SERVICE_ID
argument_list|,
name|this
operator|.
name|feed
operator|.
name|getServiceType
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|buildContent
specifier|private
name|String
name|buildContent
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|XmlWriter
name|xmlWriter
init|=
operator|new
name|XmlWriter
argument_list|(
name|writer
argument_list|,
name|INTERNAL_ENCODING
argument_list|)
decl_stmt|;
name|this
operator|.
name|feed
operator|.
name|generateAtom
argument_list|(
name|xmlWriter
argument_list|,
name|this
operator|.
name|config
operator|.
name|getExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * @return - the wrapped feed      */
DECL|method|getFeed
specifier|public
name|BaseFeed
name|getFeed
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getFeed
argument_list|()
return|;
block|}
block|}
end_class
end_unit
