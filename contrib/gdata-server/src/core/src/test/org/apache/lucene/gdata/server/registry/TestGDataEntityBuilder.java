begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.server.registry
package|package
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ServerBaseEntry
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
name|GDataEntityBuilder
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
name|Entry
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
name|Source
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
begin_comment
comment|/**   * @author Simon Willnauer   *   */
end_comment
begin_class
DECL|class|TestGDataEntityBuilder
specifier|public
class|class
name|TestGDataEntityBuilder
extends|extends
name|TestCase
block|{
DECL|field|fileDir
specifier|private
specifier|static
name|String
name|fileDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"lucene.common.dir"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|incomingFeed
specifier|private
specifier|static
name|File
name|incomingFeed
init|=
operator|new
name|File
argument_list|(
name|fileDir
argument_list|,
literal|"contrib/gdata-server/src/core/src/test/org/apache/lucene/gdata/server/registry/TestEntityBuilderIncomingFeed.xml"
argument_list|)
decl_stmt|;
DECL|field|incomingEntry
specifier|private
specifier|static
name|File
name|incomingEntry
init|=
operator|new
name|File
argument_list|(
name|fileDir
argument_list|,
literal|"contrib/gdata-server/src/core/src/test/org/apache/lucene/gdata/server/registry/TestEntityBuilderIncomingEntry.xml"
argument_list|)
decl_stmt|;
DECL|field|feedTitleFromXML
specifier|private
specifier|static
name|String
name|feedTitleFromXML
init|=
literal|"Simon Willnauer"
decl_stmt|;
DECL|field|entrySummaryFromXML
specifier|private
specifier|static
name|String
name|entrySummaryFromXML
init|=
literal|"When: 2006-12-23 to 2006-12-31 America/Los_Angeles"
decl_stmt|;
DECL|field|reg
specifier|private
specifier|static
name|GDataServerRegistry
name|reg
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
DECL|field|reader
specifier|private
name|Reader
name|reader
decl_stmt|;
DECL|field|feedID
specifier|private
specifier|static
name|String
name|feedID
init|=
literal|"myFeed"
decl_stmt|;
DECL|field|config
specifier|private
name|ProvidedServiceConfig
name|config
decl_stmt|;
DECL|field|feedType
specifier|private
specifier|static
name|Class
name|feedType
init|=
name|Feed
operator|.
name|class
decl_stmt|;
DECL|field|entryType
specifier|private
specifier|static
name|Class
name|entryType
init|=
name|Entry
operator|.
name|class
decl_stmt|;
comment|/**       * @see junit.framework.TestCase#setUp()       */
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|config
operator|=
operator|new
name|ProvidedServiceConfig
argument_list|()
expr_stmt|;
name|this
operator|.
name|config
operator|.
name|setFeedType
argument_list|(
name|feedType
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|.
name|setEntryType
argument_list|(
name|entryType
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|.
name|setExtensionProfile
argument_list|(
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|reg
operator|.
name|registerService
argument_list|(
name|this
operator|.
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**       * @see junit.framework.TestCase#tearDown()       */
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reg
operator|.
name|flushRegistry
argument_list|()
expr_stmt|;
name|this
operator|.
name|reader
operator|=
literal|null
expr_stmt|;
block|}
comment|/**       * Test method for 'org.apache.lucene.gdata.data.GDataEntityBuilder.buildFeed(String, Reader)'       */
DECL|method|testBuildFeedStringReader
specifier|public
name|void
name|testBuildFeedStringReader
parameter_list|()
throws|throws
name|ParseException
throws|,
name|IOException
block|{
name|this
operator|.
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|incomingFeed
argument_list|)
expr_stmt|;
name|BaseFeed
name|feed
init|=
name|GDataEntityBuilder
operator|.
name|buildFeed
argument_list|(
name|this
operator|.
name|reader
argument_list|,
name|this
operator|.
name|config
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|feed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"feed title"
argument_list|,
name|feed
operator|.
name|getTitle
argument_list|()
operator|.
name|getPlainText
argument_list|()
argument_list|,
name|feedTitleFromXML
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test method for 'org.apache.lucene.gdata.data.GDataEntityBuilder.buildEntry(String, Reader)'       */
DECL|method|testBuildEntryStringReader
specifier|public
name|void
name|testBuildEntryStringReader
parameter_list|()
throws|throws
name|ParseException
throws|,
name|IOException
block|{
name|this
operator|.
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|incomingEntry
argument_list|)
expr_stmt|;
name|BaseEntry
name|entry
init|=
name|GDataEntityBuilder
operator|.
name|buildEntry
argument_list|(
name|this
operator|.
name|reader
argument_list|,
name|this
operator|.
name|config
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"entry summary"
argument_list|,
name|entry
operator|.
name|getSummary
argument_list|()
operator|.
name|getPlainText
argument_list|()
argument_list|,
name|entrySummaryFromXML
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
