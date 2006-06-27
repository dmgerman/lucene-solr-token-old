begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.data
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|client
operator|.
name|Service
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
name|BaseEntry
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
name|data
operator|.
name|Category
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
name|DateTime
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
name|Extension
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
name|ExtensionProfile
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
name|Feed
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
name|Generator
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
name|Link
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
name|Person
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
name|TextConstruct
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
name|ParseException
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
name|ServiceException
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
name|XmlBlob
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
comment|/**  * The GData-Server uses the GDATA-Client API for an interal representation of  * entries. These entities have dynamic elements like Links being generated  * using the requested URL.<br/> Some components of the server also need  * additional infomation like the service type  * {@link org.apache.lucene.gdata.server.registry.ProvidedService} of the feed.  * All these information are  * encapsulated in the ServerBaseFeed decoration a concrete subl class of<tt>BaseFeed</tt>. The type of the   * {@link com.google.gdata.data.BaseEntry} contained it this feed will be passed to the ServerBaseFeed  * at creation time via the constructor. To retrieve the original entry call  * {@link ServerBaseFeed#getFeed()} returns a  * {@link com.google.gdata.data.BaseFeed} instance which can be casted into the  * actual type. To use the ServerBaseEntry for generation a provided format like  * RSS/ATOM the corresponding {@link com.google.gdata.data.ExtensionProfile} has  * to be provided to the generation method.  *   * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|ServerBaseFeed
specifier|public
class|class
name|ServerBaseFeed
block|{
DECL|field|serviceType
specifier|private
name|String
name|serviceType
decl_stmt|;
DECL|field|serviceConfig
specifier|private
name|ProvidedService
name|serviceConfig
decl_stmt|;
DECL|field|account
specifier|private
name|GDataAccount
name|account
decl_stmt|;
DECL|field|feed
specifier|private
name|BaseFeed
name|feed
decl_stmt|;
comment|/**      * @return Returns the account.      */
DECL|method|getAccount
specifier|public
name|GDataAccount
name|getAccount
parameter_list|()
block|{
return|return
name|this
operator|.
name|account
return|;
block|}
comment|/**      * @param account The account to set.      */
DECL|method|setAccount
specifier|public
name|void
name|setAccount
parameter_list|(
name|GDataAccount
name|account
parameter_list|)
block|{
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
block|}
comment|/**      * Creates a new ServerBaseFeed and decorates a basic instance of {@link Feed}      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ServerBaseFeed
specifier|public
name|ServerBaseFeed
parameter_list|()
block|{
name|this
operator|.
name|feed
operator|=
operator|new
name|Feed
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param feed - the feed to decorate      *                  */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ServerBaseFeed
specifier|public
name|ServerBaseFeed
parameter_list|(
name|BaseFeed
name|feed
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|=
name|feed
expr_stmt|;
block|}
comment|/**      * @return Returns the feed.      */
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
return|;
block|}
comment|/**      * @param feed The feed to set.      */
DECL|method|setFeed
specifier|public
name|void
name|setFeed
parameter_list|(
name|BaseFeed
name|feed
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|=
name|feed
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#declareExtensions(com.google.gdata.data.ExtensionProfile)      */
DECL|method|declareExtensions
specifier|public
name|void
name|declareExtensions
parameter_list|(
name|ExtensionProfile
name|extProfile
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|declareExtensions
argument_list|(
name|extProfile
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param link -      *            a link added to the link list of the feed      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|addLink
specifier|public
name|void
name|addLink
parameter_list|(
specifier|final
name|Link
name|link
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|getLinks
argument_list|()
operator|.
name|add
argument_list|(
name|link
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param collection -      *            a collection of<code>Link</code> instance to be added to      *            the feeds link list      */
DECL|method|addLinks
specifier|public
name|void
name|addLinks
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Link
argument_list|>
name|collection
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|getLinks
argument_list|()
operator|.
name|addAll
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return - the name of the service related of the feed represented by this      *         ServerBaseFeed      */
DECL|method|getServiceType
specifier|public
name|String
name|getServiceType
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceType
return|;
block|}
comment|/**      * @param serviceType -      *            the name of the service related of the feed represented by      *            this ServerBaseFeed      */
DECL|method|setServiceType
specifier|public
name|void
name|setServiceType
parameter_list|(
name|String
name|serviceType
parameter_list|)
block|{
name|this
operator|.
name|serviceType
operator|=
name|serviceType
expr_stmt|;
block|}
comment|/**      * @return - the provided service      */
DECL|method|getServiceConfig
specifier|public
name|ProvidedService
name|getServiceConfig
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceConfig
return|;
block|}
comment|/**      * @param serviceConfig - -      *            the provided service      */
DECL|method|setServiceConfig
specifier|public
name|void
name|setServiceConfig
parameter_list|(
name|ProvidedService
name|serviceConfig
parameter_list|)
block|{
name|this
operator|.
name|serviceConfig
operator|=
name|serviceConfig
expr_stmt|;
if|if
condition|(
name|serviceConfig
operator|!=
literal|null
condition|)
name|this
operator|.
name|serviceType
operator|=
name|this
operator|.
name|serviceConfig
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param person -      *            adds an author to the feed      */
DECL|method|addAuthor
specifier|public
name|void
name|addAuthor
parameter_list|(
specifier|final
name|Person
name|person
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|getAuthors
argument_list|()
operator|.
name|add
argument_list|(
name|person
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#createEntry()      */
DECL|method|createEntry
specifier|public
name|BaseEntry
name|createEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|createEntry
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#generateAtom(com.google.gdata.util.common.xml.XmlWriter, com.google.gdata.data.ExtensionProfile)      */
DECL|method|generateAtom
specifier|public
name|void
name|generateAtom
parameter_list|(
name|XmlWriter
name|arg0
parameter_list|,
name|ExtensionProfile
name|arg1
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|feed
operator|.
name|generateAtom
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#generateAtomColl(com.google.gdata.util.common.xml.XmlWriter)      */
DECL|method|generateAtomColl
specifier|public
name|void
name|generateAtomColl
parameter_list|(
name|XmlWriter
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|feed
operator|.
name|generateAtomColl
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#generateRss(com.google.gdata.util.common.xml.XmlWriter, com.google.gdata.data.ExtensionProfile)      */
DECL|method|generateRss
specifier|public
name|void
name|generateRss
parameter_list|(
name|XmlWriter
name|arg0
parameter_list|,
name|ExtensionProfile
name|arg1
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|feed
operator|.
name|generateRss
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getCanPost()      */
DECL|method|getCanPost
specifier|public
name|boolean
name|getCanPost
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getCanPost
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getEntries()      */
DECL|method|getEntries
specifier|public
name|List
name|getEntries
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getEntries
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getEntryPostLink()      */
DECL|method|getEntryPostLink
specifier|public
name|Link
name|getEntryPostLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getEntryPostLink
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getItemsPerPage()      */
DECL|method|getItemsPerPage
specifier|public
name|int
name|getItemsPerPage
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getItemsPerPage
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getSelf()      */
DECL|method|getSelf
specifier|public
name|BaseFeed
name|getSelf
parameter_list|()
throws|throws
name|IOException
throws|,
name|ServiceException
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getSelf
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getSelfLink()      */
DECL|method|getSelfLink
specifier|public
name|Link
name|getSelfLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getSelfLink
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getService()      */
DECL|method|getService
specifier|public
name|Service
name|getService
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getService
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getStartIndex()      */
DECL|method|getStartIndex
specifier|public
name|int
name|getStartIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getStartIndex
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#getTotalResults()      */
DECL|method|getTotalResults
specifier|public
name|int
name|getTotalResults
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getTotalResults
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#insert(E)      */
DECL|method|insert
specifier|public
name|BaseEntry
name|insert
parameter_list|(
name|BaseEntry
name|arg0
parameter_list|)
throws|throws
name|ServiceException
throws|,
name|IOException
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|insert
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#parseAtom(com.google.gdata.data.ExtensionProfile, java.io.InputStream)      */
DECL|method|parseAtom
specifier|public
name|void
name|parseAtom
parameter_list|(
name|ExtensionProfile
name|arg0
parameter_list|,
name|InputStream
name|arg1
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|this
operator|.
name|feed
operator|.
name|parseAtom
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#parseAtom(com.google.gdata.data.ExtensionProfile, java.io.Reader)      */
DECL|method|parseAtom
specifier|public
name|void
name|parseAtom
parameter_list|(
name|ExtensionProfile
name|arg0
parameter_list|,
name|Reader
name|arg1
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|this
operator|.
name|feed
operator|.
name|parseAtom
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#setCanPost(boolean)      */
DECL|method|setCanPost
specifier|public
name|void
name|setCanPost
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setCanPost
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#setItemsPerPage(int)      */
DECL|method|setItemsPerPage
specifier|public
name|void
name|setItemsPerPage
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setItemsPerPage
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#setService(com.google.gdata.client.Service)      */
DECL|method|setService
specifier|public
name|void
name|setService
parameter_list|(
name|Service
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setService
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#setStartIndex(int)      */
DECL|method|setStartIndex
specifier|public
name|void
name|setStartIndex
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setStartIndex
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.BaseFeed#setTotalResults(int)      */
DECL|method|setTotalResults
specifier|public
name|void
name|setTotalResults
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setTotalResults
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#addHtmlLink(java.lang.String, java.lang.String, java.lang.String)      */
DECL|method|addHtmlLink
specifier|public
name|void
name|addHtmlLink
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|,
name|String
name|arg2
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|addHtmlLink
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getAuthors()      */
DECL|method|getAuthors
specifier|public
name|List
argument_list|<
name|Person
argument_list|>
name|getAuthors
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getAuthors
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getCategories()      */
DECL|method|getCategories
specifier|public
name|Set
argument_list|<
name|Category
argument_list|>
name|getCategories
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getCategories
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getContributors()      */
DECL|method|getContributors
specifier|public
name|List
argument_list|<
name|Person
argument_list|>
name|getContributors
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getContributors
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getGenerator()      */
DECL|method|getGenerator
specifier|public
name|Generator
name|getGenerator
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getGenerator
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getHtmlLink()      */
DECL|method|getHtmlLink
specifier|public
name|Link
name|getHtmlLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getHtmlLink
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getIcon()      */
DECL|method|getIcon
specifier|public
name|String
name|getIcon
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getIcon
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getId()      */
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getId
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getLink(java.lang.String, java.lang.String)      */
DECL|method|getLink
specifier|public
name|Link
name|getLink
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getLink
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getLinks()      */
DECL|method|getLinks
specifier|public
name|List
argument_list|<
name|Link
argument_list|>
name|getLinks
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getLinks
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getLogo()      */
DECL|method|getLogo
specifier|public
name|String
name|getLogo
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getLogo
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getRights()      */
DECL|method|getRights
specifier|public
name|TextConstruct
name|getRights
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getRights
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getSubtitle()      */
DECL|method|getSubtitle
specifier|public
name|TextConstruct
name|getSubtitle
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getSubtitle
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getTitle()      */
DECL|method|getTitle
specifier|public
name|TextConstruct
name|getTitle
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getTitle
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#getUpdated()      */
DECL|method|getUpdated
specifier|public
name|DateTime
name|getUpdated
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getUpdated
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setGenerator(com.google.gdata.data.Generator)      */
DECL|method|setGenerator
specifier|public
name|void
name|setGenerator
parameter_list|(
name|Generator
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setGenerator
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setIcon(java.lang.String)      */
DECL|method|setIcon
specifier|public
name|void
name|setIcon
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setIcon
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setId(java.lang.String)      */
DECL|method|setId
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setId
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setLogo(java.lang.String)      */
DECL|method|setLogo
specifier|public
name|void
name|setLogo
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setLogo
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setRights(com.google.gdata.data.TextConstruct)      */
DECL|method|setRights
specifier|public
name|void
name|setRights
parameter_list|(
name|TextConstruct
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setRights
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setSubtitle(com.google.gdata.data.TextConstruct)      */
DECL|method|setSubtitle
specifier|public
name|void
name|setSubtitle
parameter_list|(
name|TextConstruct
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setSubtitle
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setTitle(com.google.gdata.data.TextConstruct)      */
DECL|method|setTitle
specifier|public
name|void
name|setTitle
parameter_list|(
name|TextConstruct
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setTitle
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.Source#setUpdated(com.google.gdata.data.DateTime)      */
DECL|method|setUpdated
specifier|public
name|void
name|setUpdated
parameter_list|(
name|DateTime
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setUpdated
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#addExtension(com.google.gdata.data.Extension)      */
DECL|method|addExtension
specifier|public
name|void
name|addExtension
parameter_list|(
name|Extension
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|addExtension
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#addRepeatingExtension(com.google.gdata.data.Extension)      */
DECL|method|addRepeatingExtension
specifier|public
name|void
name|addRepeatingExtension
parameter_list|(
name|Extension
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|addRepeatingExtension
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#getExtension(java.lang.Class)      */
DECL|method|getExtension
specifier|public
parameter_list|<
name|T
extends|extends
name|Extension
parameter_list|>
name|T
name|getExtension
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|arg0
parameter_list|)
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getExtension
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#getRepeatingExtension(java.lang.Class)      */
DECL|method|getRepeatingExtension
specifier|public
parameter_list|<
name|T
extends|extends
name|Extension
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getRepeatingExtension
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|arg0
parameter_list|)
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getRepeatingExtension
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#getXmlBlob()      */
DECL|method|getXmlBlob
specifier|public
name|XmlBlob
name|getXmlBlob
parameter_list|()
block|{
return|return
name|this
operator|.
name|feed
operator|.
name|getXmlBlob
argument_list|()
return|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#parseCumulativeXmlBlob(com.google.gdata.util.XmlBlob, com.google.gdata.data.ExtensionProfile, java.lang.Class)      */
DECL|method|parseCumulativeXmlBlob
specifier|public
name|void
name|parseCumulativeXmlBlob
parameter_list|(
name|XmlBlob
name|arg0
parameter_list|,
name|ExtensionProfile
name|arg1
parameter_list|,
name|Class
name|arg2
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|this
operator|.
name|feed
operator|.
name|parseCumulativeXmlBlob
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#removeExtension(java.lang.Class)      */
DECL|method|removeExtension
specifier|public
name|void
name|removeExtension
parameter_list|(
name|Class
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|removeExtension
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#removeExtension(com.google.gdata.data.Extension)      */
DECL|method|removeExtension
specifier|public
name|void
name|removeExtension
parameter_list|(
name|Extension
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|removeExtension
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#removeRepeatingExtension(com.google.gdata.data.Extension)      */
DECL|method|removeRepeatingExtension
specifier|public
name|void
name|removeRepeatingExtension
parameter_list|(
name|Extension
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|removeRepeatingExtension
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#setExtension(com.google.gdata.data.Extension)      */
DECL|method|setExtension
specifier|public
name|void
name|setExtension
parameter_list|(
name|Extension
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setExtension
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see com.google.gdata.data.ExtensionPoint#setXmlBlob(com.google.gdata.util.XmlBlob)      */
DECL|method|setXmlBlob
specifier|public
name|void
name|setXmlBlob
parameter_list|(
name|XmlBlob
name|arg0
parameter_list|)
block|{
name|this
operator|.
name|feed
operator|.
name|setXmlBlob
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
