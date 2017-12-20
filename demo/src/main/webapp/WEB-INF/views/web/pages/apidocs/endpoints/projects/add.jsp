<h2>Add Contact to Client</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>projects/add</strong></em></p>

<p>Create a project.</p>

<table>
  <tr>
    <td>HTTP Method</td>
    <td><code>POST</code></td>
  </tr>
  <tr>
    <td>Requires Authentication</td>
    <td>Yes</td>
  </tr>
</table>

<h3>Parameters</h3>

<table>
  <tbody>
  <tr>
    <td><code>client_id</code></td>
    <td></td>
    <td><span class="required"></span> The client identifier</td>
  </tr>
  <tr>
    <td><code>name</code></td>
    <td></td>
    <td>The name of the new project</td>
  </tr>
  <tr>
    <td><code>description</code></td>
    <td></td>
    <td>A description of the new project</td>
  </tr>
  </tbody>
</table>

<h3>Response fields</h3>
<table>
  <tr>
    <td><code>id</code></td>
    <td>The new project's unique identifier</td>
  </tr>
</table>
