begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A parent class for exceptions thrown by the Facets code.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetException
specifier|public
class|class
name|FacetException
extends|extends
name|IOException
block|{
DECL|method|FacetException
specifier|public
name|FacetException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|FacetException
specifier|public
name|FacetException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|FacetException
specifier|public
name|FacetException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|FacetException
specifier|public
name|FacetException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
