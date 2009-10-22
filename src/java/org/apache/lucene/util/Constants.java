begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|LucenePackage
import|;
end_import
begin_comment
comment|/**  * Some useful constants.  **/
end_comment
begin_class
DECL|class|Constants
specifier|public
specifier|final
class|class
name|Constants
block|{
DECL|method|Constants
specifier|private
name|Constants
parameter_list|()
block|{}
comment|// can't construct
comment|/** The value of<tt>System.getProperty("java.version")<tt>. **/
DECL|field|JAVA_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_VERSION
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
decl_stmt|;
comment|/** True iff this is Java version 1.1. */
DECL|field|JAVA_1_1
specifier|public
specifier|static
specifier|final
name|boolean
name|JAVA_1_1
init|=
name|JAVA_VERSION
operator|.
name|startsWith
argument_list|(
literal|"1.1."
argument_list|)
decl_stmt|;
comment|/** True iff this is Java version 1.2. */
DECL|field|JAVA_1_2
specifier|public
specifier|static
specifier|final
name|boolean
name|JAVA_1_2
init|=
name|JAVA_VERSION
operator|.
name|startsWith
argument_list|(
literal|"1.2."
argument_list|)
decl_stmt|;
comment|/** True iff this is Java version 1.3. */
DECL|field|JAVA_1_3
specifier|public
specifier|static
specifier|final
name|boolean
name|JAVA_1_3
init|=
name|JAVA_VERSION
operator|.
name|startsWith
argument_list|(
literal|"1.3."
argument_list|)
decl_stmt|;
comment|/** The value of<tt>System.getProperty("os.name")<tt>. **/
DECL|field|OS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OS_NAME
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
comment|/** True iff running on Linux. */
DECL|field|LINUX
specifier|public
specifier|static
specifier|final
name|boolean
name|LINUX
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"Linux"
argument_list|)
decl_stmt|;
comment|/** True iff running on Windows. */
DECL|field|WINDOWS
specifier|public
specifier|static
specifier|final
name|boolean
name|WINDOWS
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
comment|/** True iff running on SunOS. */
DECL|field|SUN_OS
specifier|public
specifier|static
specifier|final
name|boolean
name|SUN_OS
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"SunOS"
argument_list|)
decl_stmt|;
DECL|field|OS_ARCH
specifier|public
specifier|static
specifier|final
name|String
name|OS_ARCH
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
decl_stmt|;
DECL|field|OS_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|OS_VERSION
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.version"
argument_list|)
decl_stmt|;
DECL|field|JAVA_VENDOR
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_VENDOR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
decl_stmt|;
comment|// NOTE: this logic may not be correct; if you know of a
comment|// more reliable approach please raise it on java-dev!
DECL|field|JRE_IS_64BIT
specifier|public
specifier|static
specifier|final
name|boolean
name|JRE_IS_64BIT
decl_stmt|;
static|static
block|{
name|String
name|x
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|null
condition|)
block|{
name|JRE_IS_64BIT
operator|=
name|x
operator|.
name|indexOf
argument_list|(
literal|"64"
argument_list|)
operator|!=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|OS_ARCH
operator|!=
literal|null
operator|&&
name|OS_ARCH
operator|.
name|indexOf
argument_list|(
literal|"64"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|JRE_IS_64BIT
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|JRE_IS_64BIT
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
comment|// this method prevents inlining the final version constant in compiled classes,
comment|// see: http://www.javaworld.com/community/node/3400
DECL|method|ident
specifier|private
specifier|static
name|String
name|ident
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|LUCENE_MAIN_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|LUCENE_MAIN_VERSION
init|=
name|ident
argument_list|(
literal|"3.0"
argument_list|)
decl_stmt|;
DECL|field|LUCENE_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|LUCENE_VERSION
decl_stmt|;
static|static
block|{
name|Package
name|pkg
init|=
name|LucenePackage
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|v
init|=
operator|(
name|pkg
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|pkg
operator|.
name|getImplementationVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|LUCENE_MAIN_VERSION
operator|+
literal|"-dev"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|indexOf
argument_list|(
name|LUCENE_MAIN_VERSION
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|v
operator|=
name|v
operator|+
literal|" ["
operator|+
name|LUCENE_MAIN_VERSION
operator|+
literal|"]"
expr_stmt|;
block|}
name|LUCENE_VERSION
operator|=
name|ident
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
