begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
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
name|FileFilter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Accepts any file whose name matches the pattern  * @version $Id$  */
end_comment
begin_class
DECL|class|RegexFileFilter
specifier|public
specifier|final
class|class
name|RegexFileFilter
implements|implements
name|FileFilter
block|{
DECL|field|pattern
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|method|RegexFileFilter
specifier|public
name|RegexFileFilter
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
name|this
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|RegexFileFilter
specifier|public
name|RegexFileFilter
parameter_list|(
name|Pattern
name|regex
parameter_list|)
block|{
name|pattern
operator|=
name|regex
expr_stmt|;
block|}
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|f
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|matcher
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"regex:"
operator|+
name|pattern
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
