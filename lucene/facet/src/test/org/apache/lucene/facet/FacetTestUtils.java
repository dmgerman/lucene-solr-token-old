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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResult
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResultNode
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|FacetTestUtils
specifier|public
class|class
name|FacetTestUtils
block|{
DECL|method|toSimpleString
specifier|public
specifier|static
name|String
name|toSimpleString
parameter_list|(
name|FacetResult
name|fr
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toSimpleString
argument_list|(
literal|0
argument_list|,
name|sb
argument_list|,
name|fr
operator|.
name|getFacetResultNode
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toSimpleString
specifier|private
specifier|static
name|void
name|toSimpleString
parameter_list|(
name|int
name|depth
parameter_list|,
name|StringBuilder
name|sb
parameter_list|,
name|FacetResultNode
name|node
parameter_list|,
name|String
name|indent
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|indent
operator|+
name|node
operator|.
name|label
operator|.
name|components
index|[
name|depth
index|]
operator|+
literal|" ("
operator|+
operator|(
name|int
operator|)
name|node
operator|.
name|value
operator|+
literal|")\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|childNode
range|:
name|node
operator|.
name|subResults
control|)
block|{
name|toSimpleString
argument_list|(
name|depth
operator|+
literal|1
argument_list|,
name|sb
argument_list|,
name|childNode
argument_list|,
name|indent
operator|+
literal|"  "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
