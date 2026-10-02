// Supabase Edge Function: create-razorpay-order
// Deployed to: https://<project-ref>.supabase.co/functions/v1/create-razorpay-order

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  // Handle CORS preflight
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const { bookingId, amountInPaise, currency = "INR", receipt } = await req.json();

    if (!amountInPaise || !bookingId) {
      return new Response(
        JSON.stringify({ error: "Missing required parameters: bookingId or amountInPaise" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const keyId = Deno.env.get("RAZORPAY_KEY_ID") || "rzp_test_samplekey1234567";
    const keySecret = Deno.env.get("RAZORPAY_KEY_SECRET") || "sample_razorpay_secret";

    // Call Razorpay Orders API
    const authHeader = "Basic " + btoa(`${keyId}:${keySecret}`);
    const orderPayload = {
      amount: amountInPaise,
      currency: currency,
      receipt: receipt || `receipt_${bookingId}`,
      notes: {
        booking_id: bookingId.toString(),
        hub_city: "Hisar, Haryana",
      },
    };

    const rzpResponse = await fetch("https://api.razorpay.com/v1/orders", {
      method: "POST",
      headers: {
        "Authorization": authHeader,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(orderPayload),
    });

    const orderData = await rzpResponse.json();

    if (!rzpResponse.ok) {
      return new Response(
        JSON.stringify({ error: orderData.error || "Failed to create order on Razorpay" }),
        { status: rzpResponse.status, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    return new Response(
      JSON.stringify({
        orderId: orderData.id,
        amount: orderData.amount,
        currency: orderData.currency,
        keyId: keyId,
      }),
      { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (error: any) {
    return new Response(
      JSON.stringify({ error: error.message || "Internal server error" }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
