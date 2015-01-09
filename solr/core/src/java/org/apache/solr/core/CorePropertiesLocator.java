begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|SolrException
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
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Properties
import|;
end_import
begin_comment
comment|/**  * Persists CoreDescriptors as properties files  */
end_comment
begin_class
DECL|class|CorePropertiesLocator
specifier|public
class|class
name|CorePropertiesLocator
implements|implements
name|CoresLocator
block|{
DECL|field|PROPERTIES_FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTIES_FILENAME
init|=
literal|"core.properties"
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CoresLocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rootDirectory
specifier|private
specifier|final
name|File
name|rootDirectory
decl_stmt|;
DECL|method|CorePropertiesLocator
specifier|public
name|CorePropertiesLocator
parameter_list|(
name|String
name|coreDiscoveryRoot
parameter_list|)
block|{
name|this
operator|.
name|rootDirectory
operator|=
operator|new
name|File
argument_list|(
name|coreDiscoveryRoot
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Config-defined core root directory: {}"
argument_list|,
name|this
operator|.
name|rootDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
for|for
control|(
name|CoreDescriptor
name|cd
range|:
name|coreDescriptors
control|)
block|{
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
argument_list|,
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|propFile
operator|.
name|exists
argument_list|()
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Could not create a new core in "
operator|+
name|cd
operator|.
name|getInstanceDir
argument_list|()
operator|+
literal|"as another core is already defined there"
argument_list|)
throw|;
name|writePropertiesFile
argument_list|(
name|cd
argument_list|,
name|propFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO, this isn't atomic!  If we crash in the middle of a rename, we
comment|// could end up with two cores with identical names, in which case one of
comment|// them won't start up.  Are we happy with this?
annotation|@
name|Override
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
for|for
control|(
name|CoreDescriptor
name|cd
range|:
name|coreDescriptors
control|)
block|{
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
argument_list|,
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
name|writePropertiesFile
argument_list|(
name|cd
argument_list|,
name|propFile
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writePropertiesFile
specifier|private
name|void
name|writePropertiesFile
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|,
name|File
name|propfile
parameter_list|)
block|{
name|Properties
name|p
init|=
name|buildCoreProperties
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|Writer
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|propfile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|os
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|propfile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|p
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"Written by CorePropertiesLocator"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Couldn't persist core properties to {}: {}"
argument_list|,
name|propfile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
if|if
condition|(
name|coreDescriptors
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|CoreDescriptor
name|cd
range|:
name|coreDescriptors
control|)
block|{
if|if
condition|(
name|cd
operator|==
literal|null
condition|)
continue|continue;
name|File
name|instanceDir
init|=
operator|new
name|File
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|propertiesFile
init|=
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
name|propertiesFile
operator|.
name|renameTo
argument_list|(
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
name|PROPERTIES_FILENAME
operator|+
literal|".unloaded"
argument_list|)
argument_list|)
expr_stmt|;
comment|// This is a best-effort: the core.properties file may already have been
comment|// deleted by the core unload, so we don't worry about checking if the
comment|// rename has succeeded.
block|}
block|}
annotation|@
name|Override
DECL|method|rename
specifier|public
name|void
name|rename
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|oldCD
parameter_list|,
name|CoreDescriptor
name|newCD
parameter_list|)
block|{
name|persist
argument_list|(
name|cc
argument_list|,
name|newCD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|swap
specifier|public
name|void
name|swap
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd1
parameter_list|,
name|CoreDescriptor
name|cd2
parameter_list|)
block|{
name|persist
argument_list|(
name|cc
argument_list|,
name|cd1
argument_list|,
name|cd2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|discover
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|discover
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Looking for core definitions underneath {}"
argument_list|,
name|rootDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|cds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootDirectory
operator|.
name|canRead
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Solr home '"
operator|+
name|rootDirectory
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' doesn't have read permissions"
argument_list|)
throw|;
block|}
name|discoverUnder
argument_list|(
name|rootDirectory
argument_list|,
name|cds
argument_list|,
name|cc
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Found {} core definitions"
argument_list|,
name|cds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cds
return|;
block|}
DECL|method|discoverUnder
specifier|private
name|void
name|discoverUnder
parameter_list|(
name|File
name|root
parameter_list|,
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|cds
parameter_list|,
name|CoreContainer
name|cc
parameter_list|)
block|{
for|for
control|(
name|File
name|child
range|:
name|root
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|.
name|canRead
argument_list|()
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Cannot read directory or file during core discovery '"
operator|+
name|child
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' during core discovery. Skipping"
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|File
name|propertiesFile
init|=
operator|new
name|File
argument_list|(
name|child
argument_list|,
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertiesFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|CoreDescriptor
name|cd
init|=
name|buildCoreDescriptor
argument_list|(
name|propertiesFile
argument_list|,
name|cc
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Found core {} in {}"
argument_list|,
name|cd
operator|.
name|getName
argument_list|()
argument_list|,
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
expr_stmt|;
name|cds
operator|.
name|add
argument_list|(
name|cd
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|child
operator|.
name|isDirectory
argument_list|()
condition|)
name|discoverUnder
argument_list|(
name|child
argument_list|,
name|cds
argument_list|,
name|cc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildCoreDescriptor
specifier|protected
name|CoreDescriptor
name|buildCoreDescriptor
parameter_list|(
name|File
name|propertiesFile
parameter_list|,
name|CoreContainer
name|cc
parameter_list|)
block|{
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|instanceDir
init|=
name|propertiesFile
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|Properties
name|coreProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|propertiesFile
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|createName
argument_list|(
name|coreProperties
argument_list|,
name|instanceDir
argument_list|)
decl_stmt|;
return|return
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
name|name
argument_list|,
name|instanceDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|coreProperties
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Couldn't load core descriptor from {}:{}"
argument_list|,
name|propertiesFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fis
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createName
specifier|protected
specifier|static
name|String
name|createName
parameter_list|(
name|Properties
name|p
parameter_list|,
name|File
name|instanceDir
parameter_list|)
block|{
return|return
name|p
operator|.
name|getProperty
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
name|instanceDir
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|buildCoreProperties
specifier|protected
name|Properties
name|buildCoreProperties
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|putAll
argument_list|(
name|cd
operator|.
name|getPersistableStandardProperties
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|putAll
argument_list|(
name|cd
operator|.
name|getPersistableUserProperties
argument_list|()
argument_list|)
expr_stmt|;
comment|// We don't persist the instance directory, as that's defined by the location
comment|// of the properties file.
name|p
operator|.
name|remove
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_INSTDIR
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class
end_unit
