begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|index
operator|.
name|MergePolicy
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import
begin_comment
comment|/**   * A "test the test" sanity check using reflection to ensure that   * {@linke RandomMergePolicy} is working as expected  */
end_comment
begin_class
DECL|class|TestRandomMergePolicy
specifier|public
class|class
name|TestRandomMergePolicy
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Ensure every MP method is overridden by RMP     * (future proof ourselves against new methods being added to MP)    */
DECL|method|testMethodOverride
specifier|public
name|void
name|testMethodOverride
parameter_list|()
block|{
name|Class
name|rmp
init|=
name|RandomMergePolicy
operator|.
name|class
decl_stmt|;
for|for
control|(
name|Method
name|meth
range|:
name|rmp
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
comment|// ignore things like hashCode, equals, etc...
name|meth
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|equals
argument_list|(
name|Object
operator|.
name|class
argument_list|)
comment|// can't do anything about it regardless of what class declares it
operator|||
name|Modifier
operator|.
name|isFinal
argument_list|(
name|meth
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertEquals
argument_list|(
literal|"method not overridden by RandomMergePolicy: "
operator|+
name|meth
operator|.
name|toGenericString
argument_list|()
argument_list|,
name|rmp
argument_list|,
name|meth
operator|.
name|getDeclaringClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Ensure any "getter" methods return the same value as    * the wrapped MP    * (future proof ourselves against new final getter/setter pairs being     * added to MP w/o dealing with them in the RMP Constructor)    */
DECL|method|testGetters
specifier|public
name|void
name|testGetters
parameter_list|()
throws|throws
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|RandomMergePolicy
name|rmp
init|=
operator|new
name|RandomMergePolicy
argument_list|()
decl_stmt|;
name|Class
name|mp
init|=
name|MergePolicy
operator|.
name|class
decl_stmt|;
for|for
control|(
name|Method
name|meth
range|:
name|mp
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|meth
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
operator|&&
operator|(
literal|0
operator|==
name|meth
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"MergePolicy getter gave diff results for RandomMergePolicy and the policy it wrapped: "
operator|+
name|meth
operator|.
name|toGenericString
argument_list|()
argument_list|,
name|meth
operator|.
name|invoke
argument_list|(
name|rmp
argument_list|)
argument_list|,
name|meth
operator|.
name|invoke
argument_list|(
name|rmp
operator|.
name|inner
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
