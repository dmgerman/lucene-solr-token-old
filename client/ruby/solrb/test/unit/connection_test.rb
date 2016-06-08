# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'test/unit'
require 'solr'
require 'solr_mock_base'

class ConnectionTest < SolrMockBaseTestCase
  def test_mock
    connection = Connection.new("http://localhost:9999")
    set_post_return("foo")
    assert_equal "foo", connection.post(Solr::Request::Update.new)
  end

  def test_connection_initialize
    connection = Solr::Connection.new("http://localhost:8983/solr")
    assert_equal 'localhost', connection.url.host
    assert_equal 8983, connection.url.port
    assert_equal '/solr', connection.url.path
  end

  def test_non_standard_context
    connection = Solr::Connection.new("http://localhost:8983/index")
    assert_equal '/index', connection.url.path
  end

  def test_xml_response
    connection = Connection.new("http://localhost:9999")
    set_post_return "<bogus/>"
    response = connection.send(Solr::Request::Update.new)
    assert_equal "<bogus/>", response.raw_response
  end
  
  def test_ruby_response
    connection = Connection.new("http://localhost:9999")
    set_post_return "{}"
    response = connection.send(Solr::Request::Select.new('foo'))
    assert_equal "{}", response.raw_response
  end
end
