begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|ServiceConfigurationError
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
name|SPIClassIterator
import|;
end_import
begin_comment
comment|/**  * Helper class for loading named SPIs from classpath (e.g. Tokenizers, TokenStreams).  * @lucene.internal  */
end_comment
begin_class
DECL|class|AnalysisSPILoader
specifier|final
class|class
name|AnalysisSPILoader
parameter_list|<
name|S
extends|extends
name|AbstractAnalysisFactory
parameter_list|>
block|{
DECL|field|services
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
name|services
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|clazz
specifier|private
specifier|final
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
decl_stmt|;
DECL|field|suffixes
specifier|private
specifier|final
name|String
index|[]
name|suffixes
decl_stmt|;
DECL|method|AnalysisSPILoader
specifier|public
name|AnalysisSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|new
name|String
index|[]
block|{
name|clazz
operator|.
name|getSimpleName
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|AnalysisSPILoader
specifier|public
name|AnalysisSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|new
name|String
index|[]
block|{
name|clazz
operator|.
name|getSimpleName
argument_list|()
block|}
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
DECL|method|AnalysisSPILoader
specifier|public
name|AnalysisSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|String
index|[]
name|suffixes
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|suffixes
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AnalysisSPILoader
specifier|public
name|AnalysisSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|String
index|[]
name|suffixes
parameter_list|,
name|ClassLoader
name|classloader
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|suffixes
operator|=
name|suffixes
expr_stmt|;
comment|// if clazz' classloader is not a parent of the given one, we scan clazz's classloader, too:
specifier|final
name|ClassLoader
name|clazzClassloader
init|=
name|clazz
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazzClassloader
operator|!=
literal|null
operator|&&
operator|!
name|SPIClassIterator
operator|.
name|isParentClassLoader
argument_list|(
name|clazzClassloader
argument_list|,
name|classloader
argument_list|)
condition|)
block|{
name|reload
argument_list|(
name|clazzClassloader
argument_list|)
expr_stmt|;
block|}
name|reload
argument_list|(
name|classloader
argument_list|)
expr_stmt|;
block|}
comment|/**     * Reloads the internal SPI list from the given {@link ClassLoader}.    * Changes to the service list are visible after the method ends, all    * iterators (e.g., from {@link #availableServices()},...) stay consistent.     *     *<p><b>NOTE:</b> Only new service providers are added, existing ones are    * never removed or replaced.    *     *<p><em>This method is expensive and should only be called for discovery    * of new service providers on the given classpath/classloader!</em>    */
DECL|method|reload
specifier|public
specifier|synchronized
name|void
name|reload
parameter_list|(
name|ClassLoader
name|classloader
parameter_list|)
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
name|services
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
argument_list|(
name|this
operator|.
name|services
argument_list|)
decl_stmt|;
specifier|final
name|SPIClassIterator
argument_list|<
name|S
argument_list|>
name|loader
init|=
name|SPIClassIterator
operator|.
name|get
argument_list|(
name|clazz
argument_list|,
name|classloader
argument_list|)
decl_stmt|;
while|while
condition|(
name|loader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|service
init|=
name|loader
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|clazzName
init|=
name|service
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|suffix
range|:
name|suffixes
control|)
block|{
if|if
condition|(
name|clazzName
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
name|name
operator|=
name|clazzName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clazzName
operator|.
name|length
argument_list|()
operator|-
name|suffix
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServiceConfigurationError
argument_list|(
literal|"The class name "
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|+
literal|" has wrong suffix, allowed are: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|suffixes
argument_list|)
argument_list|)
throw|;
block|}
comment|// only add the first one for each name, later services will be ignored
comment|// this allows to place services before others in classpath to make
comment|// them used instead of others
comment|//
comment|// TODO: Should we disallow duplicate names here?
comment|// Allowing it may get confusing on collisions, as different packages
comment|// could contain same factory class, which is a naming bug!
comment|// When changing this be careful to allow reload()!
if|if
condition|(
operator|!
name|services
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|services
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|services
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
DECL|method|newInstance
specifier|public
name|S
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|service
init|=
name|lookupClass
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|service
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SPI class of type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" with name '"
operator|+
name|name
operator|+
literal|"' cannot be instantiated. "
operator|+
literal|"This is likely due to a misconfiguration of the java class '"
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|+
literal|"': "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|lookupClass
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|lookupClass
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|service
init|=
name|services
operator|.
name|get
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
return|return
name|service
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A SPI class of type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" with name '"
operator|+
name|name
operator|+
literal|"' does not exist. "
operator|+
literal|"You need to add the corresponding JAR file supporting this SPI to your classpath."
operator|+
literal|"The current classpath supports the following names: "
operator|+
name|availableServices
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|availableServices
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|availableServices
parameter_list|()
block|{
return|return
name|services
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
end_class
end_unit
